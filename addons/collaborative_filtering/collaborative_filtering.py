from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
from sklearn.decomposition import TruncatedSVD
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.neighbors import NearestNeighbors
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

app = Flask(__name__)

# Konfiguration der Datenbankverbindung
DATABASE_URI = 'mysql+pymysql://root:password@127.0.0.1/mensaHub'
engine = create_engine(DATABASE_URI)
Session = sessionmaker(bind=engine)
session = Session()

# Funktion zum Laden der Daten aus der Datenbank
def load_data():
    query = "SELECT user_user_id AS user_id, meal_name AS meal, rating FROM ratings"
    df = pd.read_sql(query, con=engine)
    return df

# Funktion zur Vorhersage
def predict_rating_optimized(user_id, meal, user_meal_matrix, matrix_svd, user_similarity_df, k=10):
    knn_optimized = NearestNeighbors(metric='cosine', algorithm='brute', n_neighbors=k)
    knn_optimized.fit(matrix_svd)
    
    if meal in user_meal_matrix.columns:
        num_samples_fit = matrix_svd.shape[0]
        k = min(k, num_samples_fit)
        
        distances, indices = knn_optimized.kneighbors(matrix_svd[user_meal_matrix.index.get_loc(user_id)].reshape(1, -1), n_neighbors=k)
        similar_users = user_meal_matrix.index[indices.flatten()]
        meal_ratings_by_similar_users = user_meal_matrix.loc[similar_users, meal]
        valid_ratings = meal_ratings_by_similar_users.dropna()
        
        if valid_ratings.empty:
            global_meal_mean = user_meal_matrix.mean().mean()
            return global_meal_mean, "Niedrig"
        
        weighted_sum = np.dot(user_similarity_df.loc[user_id, valid_ratings.index], valid_ratings)
        similarity_sum = np.sum(np.abs(user_similarity_df.loc[user_id, valid_ratings.index]))
        
        predicted_rating = weighted_sum / (similarity_sum + 1e-9)
        
        avg_similarity = similarity_sum / len(valid_ratings)
        
        if avg_similarity > 0.75:
            trust_score = "Sehr hoch"
        elif avg_similarity > 0.5:
            trust_score = "Hoch"
        elif avg_similarity > 0.25:
            trust_score = "Mittel"
        else:
            trust_score = "Niedrig"
        
        return predicted_rating.item(), trust_score
    else:
        raise ValueError(f"Das Gericht '{meal}' existiert nicht in den Daten.")

@app.route('/predict', methods=['POST'])
def predict():
    # Erhaltene JSON-Daten
    data = request.json
    
    # Daten aus der Datenbank laden
    df = load_data()
    
    # Benutzermatrix erstellen (Zeilen: Benutzer, Spalten: Gerichte)
    user_meal_matrix = df.pivot_table(index='user_id', columns='meal', values='rating')
    
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
    
    # Vorhersagen sammeln
    results = []
    for item in data:
        user_id = item['userId']
        meal = item['meal']
        try:
            predicted_rating, trust_score = predict_rating_optimized(user_id, meal, user_meal_matrix, matrix_svd, user_similarity_df)
            results.append({
                'userId': user_id,
                'meal': meal,
                'predicted_rating': predicted_rating,
                'trust_score': trust_score
            })
        except ValueError as e:
            results.append({
                'userId': user_id,
                'meal': meal,
                'error': str(e)
            })
        except Exception as e:
            results.append({
                'userId': user_id,
                'meal': meal,
                'error': f"An error occurred: {str(e)}"
            })
    
    return jsonify(results)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)