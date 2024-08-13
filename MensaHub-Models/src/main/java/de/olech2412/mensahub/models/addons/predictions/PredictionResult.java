package de.olech2412.mensahub.models.addons.predictions;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionResult {

    @SerializedName("predicted_rating")
    private double predictedRating;
    @SerializedName("trust_score")
    private String trustScore;
    private int userId;
    private int mealId;
    private String mealName;

    @Override
    public String toString() {
        return "PredictionResult{" +
                "predictedRating=" + predictedRating +
                ", trustScore='" + trustScore + '\'' +
                ", userId=" + userId +
                ", mealId=" + mealId +
                ", mealName='" + mealName + '\'' +
                '}';
    }
}
