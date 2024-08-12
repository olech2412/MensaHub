from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
from sklearn.decomposition import TruncatedSVD
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.neighbors import NearestNeighbors
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os
import re

app = Flask(__name__)

# Konfiguration der Datenbankverbindung aus Umgebungsvariablen
DB_USER = os.getenv('DB_USER', 'root')
DB_PASSWORD = os.getenv('DB_PASSWORD', 'password')
DB_HOST = os.getenv('DB_HOST', '127.0.0.1')
DB_NAME = os.getenv('DB_NAME', 'mensaHub')

DATABASE_URI = f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}/{DB_NAME}'
engine = create_engine(DATABASE_URI)
Session = sessionmaker(bind=engine)
session = Session()

# Funktion zum Laden der Daten aus der Datenbank
def load_data():
    ratings_query = "SELECT mail_user_id AS user_id, meal_name AS meal, rating, meal_id FROM ratings"
    meals_query = "SELECT id, allergens, category, description, name FROM meals"
    
    ratings_df = pd.read_sql(ratings_query, con=engine)
    meals_df = pd.read_sql(meals_query, con=engine)
    
    return ratings_df, meals_df

# Funktion zur Überprüfung von Vorlieben und Abneigungen
def check_preferences(user_id, meal_id, meals_df, user_preferences):
    meal_info = meals_df[meals_df['id'] == meal_id]
    
    if meal_info.empty:
        print(f"Warnung: Keine Informationen zur meal_id {meal_id} gefunden.")
        return True  # Keine Informationen, daher keine Präferenzen verletzt
    
    meal_info = meal_info.iloc[0]
    meal_category = meal_info['category']
    meal_allergens = meal_info['allergens']
    meal_description = meal_info['description']
    
    user_pref = user_preferences.get(user_id, {})
    
    # Kategoriepräferenz prüfen
    if 'preferred_categories' in user_pref:
        if meal_category not in user_pref['preferred_categories']:
            return False  # Kategorie nicht bevorzugt
    
    # Allergiepräferenzen prüfen
    if 'avoided_allergens' in user_pref:
        for allergen in user_pref['avoided_allergens']:
            if allergen in meal_allergens:
                return False  # Allergen gefunden
    
    # Zutatenpräferenzen in der Beschreibung prüfen
    if 'disliked_ingredients' in user_pref:
        for ingredient in user_pref['disliked_ingredients']:
            if re.search(rf'\b{ingredient}\b', meal_description, re.IGNORECASE):
                return False  # Ungeliebte Zutat gefunden
    
    return True  # Gericht entspricht den Präferenzen

# Funktion zur Vorhersage
def predict_rating_optimized(user_id, meal_id, meal_name, user_meal_matrix, matrix_svd, user_similarity_df, user_meal_mean, meals_df, user_preferences, k=10, personal_weight=0.7):
    knn_optimized = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k)
    knn_optimized.fit(matrix_svd)
    
    if meal_name in user_meal_matrix.columns:
        num_samples_fit = matrix_svd.shape[0]
        k = min(k, num_samples_fit)
        
        # Prüfung auf vorhandene Bewertung des Nutzers für das Gericht
        user_rating = user_meal_matrix.loc[user_id, meal_name]
        
        distances, indices = knn_optimized.kneighbors(matrix_svd[user_meal_matrix.index.get_loc(user_id)].reshape(1, -1), n_neighbors=k)
        similar_users = user_meal_matrix.index[indices.flatten()]
        meal_ratings_by_similar_users = user_meal_matrix.loc[similar_users, meal_name]
        valid_ratings = meal_ratings_by_similar_users.dropna()
        
        if valid_ratings.empty:
            global_meal_mean = user_meal_matrix.mean().mean()
            predicted_rating = max(1, min(5, global_meal_mean))
            trust_score = "Niedrig"
        else:
            weighted_sum = np.dot(user_similarity_df.loc[user_id, valid_ratings.index], valid_ratings)
            similarity_sum = np.sum(np.abs(user_similarity_df.loc[user_id, valid_ratings.index]))
            
            # Berechnung des Vorhersagewerts basierend auf ähnlichen Benutzern
            predicted_rating_similar_users = weighted_sum / (similarity_sum + 1e-9)
            predicted_rating_similar_users += user_meal_mean.loc[user_id]  # Mittelwert des Benutzers hinzufügen
            predicted_rating_similar_users = max(1, min(5, predicted_rating_similar_users))  # In den Bereich [1, 5] begrenzen
            
            # Kombination der persönlichen Bewertung mit den Vorhersagen anderer Benutzer
            if not np.isnan(user_rating):
                predicted_rating = personal_weight * user_rating + (1 - personal_weight) * predicted_rating_similar_users
                trust_score = "Sehr hoch"
            else:
                predicted_rating = predicted_rating_similar_users
                trust_score = "Hoch"
        
        # Überprüfen, ob das Gericht den Präferenzen des Nutzers entspricht
        if not check_preferences(user_id, meal_id, meals_df, user_preferences):
            predicted_rating = max(1, min(5, predicted_rating - 2))  # Herabsetzen der Bewertung, wenn es nicht den Präferenzen entspricht
            trust_score = "Niedrig"
        
        # Stellen Sie sicher, dass predicted_rating als nativer Python-Wert zurückgegeben wird
        return float(predicted_rating), trust_score
    else:
        raise ValueError(f"Das Gericht '{meal_name}' existiert nicht in den Daten.")

@app.route('/predict', methods=['POST'])
def predict():
    # Erhaltene JSON-Daten
    data = request.json
    
    # Daten aus der Datenbank laden
    ratings_df, meals_df = load_data()
    
    # Benutzermatrix erstellen (Zeilen: Benutzer, Spalten: Gerichte)
    user_meal_matrix = ratings_df.pivot_table(index='user_id', columns='meal', values='rating')
    
    # Mittelwerte der Benutzer-Bewertungen berechnen
    user_meal_mean = user_meal_matrix.mean(axis=1)
    
    # Differenz der Bewertungen vom Mittelwert berechnen
    user_meal_demeaned = user_meal_matrix.sub(user_meal_mean, axis=0)
    
    # Singular Value Decomposition (SVD)
    n_components = min(20, user_meal_demeaned.shape[1])  # Anzahl der Komponenten an die Anzahl der Features anpassen
    svd = TruncatedSVD(n_components=n_components, random_state=42)
    matrix_svd = svd.fit_transform(user_meal_demeaned.fillna(0))
    
    # Cosinus-Ähnlichkeit zwischen den Benutzern berechnen
    user_similarity = cosine_similarity(matrix_svd)
    
    # Benutzer-Ähnlichkeitsmatrix in DataFrame umwandeln
    user_similarity_df = pd.DataFrame(user_similarity, index=user_meal_matrix.index, columns=user_meal_matrix.index)
    
    # Beispielhafte Nutzerpräferenzen (diese könnten aus einer separaten Datenbanktabelle geladen werden)
    user_preferences = {
        452: {
            'preferred_categories': ['Vegetarisch', 'Veganes Gericht'],
            'avoided_allergens': ['Milch', 'Eier'],
            'disliked_ingredients': ['Pilze', 'Zwiebeln']
        }
    }
    
    # Vorhersagen sammeln
    results = []
    for item in data:
        user_id = item['userId']
        if 'mealId' not in item:
            # Wenn keine mealId übergeben wurde, Fehler ausgeben und keine Vorhersage machen
            results.append({
                'userId': user_id,
                'error': "mealId nicht in den übergebenen Daten enthalten"
            })
            continue
        
        meal_id = item['mealId']  # Verwende mealId für die Präferenzprüfung
        meal_name = item['meal']  # Verwende meal_name für die Bewertung
        
        try:
            predicted_rating, trust_score = predict_rating_optimized(user_id, meal_id, meal_name, user_meal_matrix, matrix_svd, user_similarity_df, user_meal_mean, meals_df, user_preferences)
            results.append({
                'userId': user_id,
                'mealId': meal_id,
                'mealName': meal_name,
                'predicted_rating': predicted_rating,
                'trust_score': trust_score
            })
        except ValueError as e:
            results.append({
                'userId': user_id,
                'mealId': meal_id,
                'mealName': meal_name,
                'error': str(e)
            })
        except Exception as e:
            results.append({
                'userId': user_id,
                'mealId': meal_id,
                'mealName': meal_name,
                'error': f"An error occurred: {str(e)}"
            })
    
    return jsonify(results)

@app.route('/health', methods=['GET'])
def health():
    return "OK", 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)