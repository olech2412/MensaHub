import logging
from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
from sklearn.decomposition import TruncatedSVD
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.neighbors import NearestNeighbors
from sqlalchemy import create_engine
import os
import re
import time
import threading
from prometheus_flask_exporter import PrometheusMetrics, Counter

app = Flask(__name__)

# Logger konfigurieren
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Prometheus Metrics initialisieren
metrics = PrometheusMetrics(app)
metrics.info("app_info", "Application info", version="1.0.0")

# Konfiguration der Datenbankverbindung aus Umgebungsvariablen
DB_USER = os.getenv('DB_USER', 'root')
DB_PASSWORD = os.getenv('DB_PASSWORD', 'password')
DB_HOST = os.getenv('DB_HOST', '127.0.0.1')
DB_NAME = os.getenv('DB_NAME', 'mensaHub')
CACHE_DURATION = int(os.getenv('CACHE_DURATION', '1200'))  # Cache-Dauer in Sekunden (default 20 Minuten)

DATABASE_URI = f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}'
engine = create_engine(DATABASE_URI)

# Cache für Daten und Zeitstempel für das letzte Laden
cache = {
    "ratings_df": None,
    "meals_df": None,
}

# Custom Metrics für Prometheus
cache_hit = Counter('cache_hit_total', 'Anzahl der Cache-Treffer')
cache_miss = Counter('cache_miss_total', 'Anzahl der Cache-Fehlschläge')

# Funktion zum Laden der Daten aus der Datenbank mit Caching
def load_data():
    logger.info("Lade Daten aus der Datenbank.")
    
    ratings_query = "SELECT mail_user_id AS user_id, meal_name AS meal, rating, meal_id FROM ratings"
    meals_query = "SELECT id, allergens, category, description, name FROM meals"
    
    ratings_df = pd.read_sql(ratings_query, con=engine)
    meals_df = pd.read_sql(meals_query, con=engine)
    
    cache["ratings_df"] = ratings_df
    cache["meals_df"] = meals_df
    
    logger.info("Daten wurden erfolgreich aktualisiert.")

# Funktion zur regelmäßigen Aktualisierung des Caches
def update_cache_periodically():
    while True:
        try:
            load_data()
        except Exception as e:
            logger.error(f"Fehler beim Aktualisieren des Caches: {e}")
        time.sleep(CACHE_DURATION)

# Starte den Cache-Aktualisierungsthread
cache_thread = threading.Thread(target=update_cache_periodically, daemon=True)
cache_thread.start()

# Initiales Laden der Daten beim Starten der API
load_data()

# API-Endpunkt Funktionen (wie z.B. predict) verwenden die gecachten Daten:
def load_cached_data():
    if cache["ratings_df"] is None or cache["meals_df"] is None:
        cache_miss.inc()
        logger.error("Cache ist leer! Lade die Daten neu.")
        load_data()
    else:
        cache_hit.inc()
    return cache["ratings_df"], cache["meals_df"]

# Funktion zum Laden der Präferenzen eines Benutzers aus der Datenbank
@metrics.summary('load_user_preferences_latency_seconds', 'Zeit, um Benutzerpräferenzen zu laden')
def load_user_preferences(user_id):
    logger.debug(f"Lade Präferenzen für Benutzer-ID: {user_id}")
    
    preferences_id_query = f"SELECT preferences_id FROM mail_users WHERE id = {user_id}"
    preferences_id_result = pd.read_sql(preferences_id_query, con=engine)
    
    if preferences_id_result.empty or preferences_id_result['preferences_id'].isnull().all():
        logger.warning(f"Keine Präferenzen für Benutzer-ID {user_id} gefunden")
        return {}  # Keine Präferenzen vorhanden
    
    preferences_id = preferences_id_result['preferences_id'].iloc[0]
    logger.debug(f"Gefundene Präferenzen-ID für Benutzer {user_id}: {preferences_id}")

    categories_query = f"SELECT disliked_categories FROM disliked_categories WHERE disliked_categories_id = {preferences_id}"
    categories_result = pd.read_sql(categories_query, con=engine)
    disliked_categories = categories_result['disliked_categories'].tolist() if not categories_result.empty else []

    allergens_query = f"SELECT avoided_allergens FROM avoided_allergens WHERE avoided_allergens_id = {preferences_id}"
    allergens_result = pd.read_sql(allergens_query, con=engine)
    avoided_allergens = allergens_result['avoided_allergens'].tolist() if not allergens_result.empty else []

    ingredients_query = f"SELECT disliked_ingedrients FROM disliked_ingredients WHERE disliked_ingredients_id = {preferences_id}"
    ingredients_result = pd.read_sql(ingredients_query, con=engine)
    disliked_ingredients = ingredients_result['disliked_ingedrients'].tolist() if not ingredients_result.empty else []

    logger.debug(f"Geladene Präferenzen für Benutzer {user_id}: {disliked_categories}, {avoided_allergens}, {disliked_ingredients}")

    user_preferences = {
        'disliked_categories': disliked_categories,
        'avoided_allergens': avoided_allergens,
        'disliked_ingredients': disliked_ingredients
    }
    
    return user_preferences

# Funktion zur Überprüfung von Vorlieben und Abneigungen
@metrics.summary('check_preferences_latency_seconds', 'Zeit, um Präferenzen zu überprüfen')
def check_preferences(user_id, meal_id, meals_df, user_preferences):
    logger.debug(f"Prüfe Präferenzen für Benutzer {user_id} und Mahlzeit {meal_id}")
    
    meal_info = meals_df[meals_df['id'] == meal_id]
    
    if meal_info.empty:
        logger.warning(f"Keine Informationen zur Mahlzeit-ID {meal_id} gefunden.")
        return True  # Keine Informationen, daher keine Präferenzen verletzt
    
    meal_info = meal_info.iloc[0]
    meal_category = meal_info['category']
    meal_allergens = meal_info['allergens']
    meal_description = meal_info['description']
    meal_name = meal_info['name']
    
    logger.debug(f"Mahlzeit-Informationen: Kategorie: {meal_category}, Allergene: {meal_allergens}, Name: {meal_name}, Beschreibung: {meal_description}")
    
    if user_preferences.get('disliked_categories') and meal_category in user_preferences['disliked_categories']:
        logger.info(f"Mahlzeit {meal_id} gehört zu einer nicht gemochten Kategorie des Benutzers {user_id}")
        return False  # Kategorie nicht bevorzugt
    
    if user_preferences.get('avoided_allergens'):
        for allergen in user_preferences['avoided_allergens']:
            if allergen in meal_allergens:
                logger.info(f"Mahlzeit {meal_id} enthält ein vermiedenes Allergen für Benutzer {user_id}: {allergen}")
                return False  # Allergen gefunden
    
    if user_preferences.get('disliked_ingredients'):
        for ingredient in user_preferences['disliked_ingredients']:
            pattern = rf'\b{re.escape(ingredient)}\w*\b'
            if re.search(pattern, meal_description, re.IGNORECASE) or re.search(pattern, meal_name, re.IGNORECASE):
                logger.info(f"Mahlzeit {meal_id} enthält eine nicht gemochte Zutat für Benutzer {user_id}: {ingredient}")
                return False  # Ungeliebte Zutat gefunden
    
    logger.debug(f"Mahlzeit {meal_id} entspricht den Präferenzen des Benutzers {user_id}")
    return True  # Gericht entspricht den Präferenzen

# Funktion zur Vorhersage
@metrics.summary('predict_rating_optimized_latency_seconds', 'Zeit, um eine Bewertung vorherzusagen')
def predict_rating_optimized(user_id, meal_id, meal_name, user_meal_matrix, matrix_svd, user_similarity_df, user_meal_mean, meals_df, user_preferences, k=10, personal_weight=0.7):
    logger.debug(f"Starte Vorhersage für Benutzer {user_id} und Mahlzeit {meal_name} ({meal_id})")
    
    knn_optimized = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k)
    knn_optimized.fit(matrix_svd)
    
    if meal_name in user_meal_matrix.columns:
        num_samples_fit = matrix_svd.shape[0]
        k = min(k, num_samples_fit)
        
        user_rating = user_meal_matrix.loc[user_id, meal_name]
        logger.debug(f"Benutzerbewertung für {meal_name}: {user_rating}")
        
        distances, indices = knn_optimized.kneighbors(matrix_svd[user_meal_matrix.index.get_loc(user_id)].reshape(1, -1), n_neighbors=k)
        similar_users = user_meal_matrix.index[indices.flatten()]
        meal_ratings_by_similar_users = user_meal_matrix.loc[similar_users, meal_name]
        valid_ratings = meal_ratings_by_similar_users.dropna()
        
        if valid_ratings.empty:
            global_meal_mean = user_meal_matrix.mean().mean()
            predicted_rating = max(1, min(5, global_meal_mean))
            trust_score = "Niedrig"
            logger.debug(f"Keine gültigen Bewertungen von ähnlichen Benutzern gefunden, Verwendung des globalen Durchschnitts: {predicted_rating}")
        else:
            weighted_sum = np.dot(user_similarity_df.loc[user_id, valid_ratings.index], valid_ratings)
            similarity_sum = np.sum(np.abs(user_similarity_df.loc[user_id, valid_ratings.index]))
            predicted_rating_similar_users = weighted_sum / (similarity_sum + 1e-9)
            predicted_rating_similar_users += user_meal_mean.loc[user_id]
            predicted_rating_similar_users = max(1, min(5, predicted_rating_similar_users))
            logger.debug(f"Vorhersage basierend auf ähnlichen Benutzern: {predicted_rating_similar_users}")
            
            if not np.isnan(user_rating):
                predicted_rating = personal_weight * user_rating + (1 - personal_weight) * predicted_rating_similar_users
                trust_score = "Sehr hoch"
                logger.debug(f"Kombinierte Vorhersage (Benutzer & Ähnlichkeit): {predicted_rating}")
            else:
                predicted_rating = predicted_rating_similar_users
                trust_score = "Hoch"
                logger.debug(f"Vorhersage basierend auf ähnlichen Benutzern ohne persönliche Bewertung: {predicted_rating}")
        
        if not check_preferences(user_id, meal_id, meals_df, user_preferences):
            predicted_rating = max(1, min(5, predicted_rating - 2))
            trust_score = "Niedrig"
            logger.debug(f"Mahlzeit entspricht nicht den Präferenzen, herabgesetzte Bewertung: {predicted_rating}")
        
        return float(predicted_rating), trust_score
    else:
        error_msg = f"Das Gericht '{meal_name}' existiert nicht in den Daten."
        logger.error(error_msg)
        raise ValueError(error_msg)

@app.route('/predict', methods=['POST'])
@metrics.histogram('predict_requests_duration_seconds', 'Dauer der Vorhersageanfragen', labels={'status': lambda r: r.status_code})
def predict():
    logger.debug("Eingehende Vorhersageanforderung")
    
    data = request.json
    logger.debug(f"Empfangene Daten: {data}")
    
    ratings_df, meals_df = load_cached_data()
    
    user_meal_matrix = ratings_df.pivot_table(index='user_id', columns='meal', values='rating')
    user_meal_mean = user_meal_matrix.mean(axis=1)
    user_meal_demeaned = user_meal_matrix.sub(user_meal_mean, axis=0)
    
    n_components = min(20, user_meal_demeaned.shape[1])
    svd = TruncatedSVD(n_components=n_components, random_state=42)
    matrix_svd = svd.fit_transform(user_meal_demeaned.fillna(0))
    
    user_similarity = cosine_similarity(matrix_svd)
    user_similarity_df = pd.DataFrame(user_similarity, index=user_meal_matrix.index, columns=user_meal_matrix.index)
    
    results = []
    for item in data:
        user_id = item['userId']
        if 'mealId' not in item:
            error_msg = "mealId nicht in den übergebenen Daten enthalten"
            logger.error(f"Fehlende mealId für Benutzer {user_id}: {error_msg}")
            results.append({'userId': user_id, 'error': error_msg})
            continue
        
        meal_id = item['mealId']
        meal_name = item['meal']
        
        logger.debug(f"Verarbeite Vorhersage für Benutzer {user_id}, Mahlzeit {meal_id} ({meal_name})")
        
        user_preferences = load_user_preferences(user_id)
        
        try:
            predicted_rating, trust_score = predict_rating_optimized(user_id, meal_id, meal_name, user_meal_matrix, matrix_svd, user_similarity_df, user_meal_mean, meals_df, user_preferences)
            results.append({
                'userId': user_id,
                'mealId': meal_id,
                'mealName': meal_name,
                'predicted_rating': predicted_rating,
                'trust_score': trust_score
            })
            logger.debug(f"Vorhersage abgeschlossen für Benutzer {user_id}, Mahlzeit {meal_id}: {predicted_rating} (Vertrauenswürdigkeit: {trust_score})")
        except ValueError as e:
            logger.error(f"Fehler bei der Verarbeitung der Mahlzeit {meal_name} für Benutzer {user_id}: {e}")
            results.append({'userId': user_id, 'mealId': meal_id, 'mealName': meal_name, 'error': str(e)})
        except Exception as e:
            logger.exception(f"Unerwarteter Fehler bei der Vorhersage für Benutzer {user_id}, Mahlzeit {meal_id}: {e}")
            results.append({'userId': user_id, 'mealId': meal_id, 'mealName': meal_name, 'error': f"An error occurred: {str(e)}"})
    
    return jsonify(results)

@app.route('/health', methods=['GET'])
def health():
    return "OK", 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8084)