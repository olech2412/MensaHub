from flask import Flask, request, jsonify
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

app = Flask("Collaborative_Filter_API")

# 1. Daten einlesen und Vorverarbeitung
data = pd.read_csv('/home/christopho/Workspace/Coding/IntelliJ/EssensGetter API/addons/MOCK_DATA_(1)_bloat_500_per_user.csv')
ratings_matrix = data.pivot(index='user_id', columns='meal_id', values='rating')
user_similarities = cosine_similarity(ratings_matrix.fillna(0))

@app.route('/predict', methods=['GET'])
def predict():
    try:
        # Parameter aus der Anfrage
        user_id = int(request.args.get('user_id'))
        meal_id = int(request.args.get('meal_id'))
        
        # Index des Benutzers finden
        if user_id not in ratings_matrix.index:
            return jsonify({"error": "User ID not found"}), 404
        if meal_id not in ratings_matrix.columns:
            return jsonify({"error": "Meal ID not found"}), 404

        user_index = ratings_matrix.index.get_loc(user_id)
        similar_users = user_similarities[user_index]
        ratings_missing_dish = ratings_matrix[meal_id]

        # Mindestähnlichkeit festlegen, um einen Benutzer als "ähnlich" zu betrachten
        min_similarity_threshold = 0.5

        # Auswahl der Benutzer mit genügend hoher Ähnlichkeit und einer Bewertung für das Gericht
        relevant_ratings = []
        for similarity, rating in zip(similar_users, ratings_missing_dish):
            if similarity >= min_similarity_threshold and not np.isnan(rating):
                relevant_ratings.append((similarity, rating))

        if not relevant_ratings:
            return jsonify({"message": "Keine ausreichend ähnlichen Benutzer für eine zuverlässige Vorhersage."}), 200
        else:
            # Berechnung der gewichteten Durchschnittsbewertung
            weighted_sum = sum(similarity * rating for similarity, rating in relevant_ratings)
            sum_of_similarities = sum(similarity for similarity, _ in relevant_ratings)
            mean_weighted_rating = weighted_sum / sum_of_similarities

            # Empfehlung basierend auf der gewichteten Bewertung
            threshold = 7
            recommendation = "true" if mean_weighted_rating >= threshold else "false"
            return jsonify({
                "user_id": user_id,
                "meal_id": meal_id,
                "predicted_rating": mean_weighted_rating,
                "recommendation": recommendation
            }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)