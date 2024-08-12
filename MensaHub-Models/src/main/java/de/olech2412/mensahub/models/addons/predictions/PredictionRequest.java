package de.olech2412.mensahub.models.addons.predictions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionRequest {
    private int userId;
    private String meal;
    private int mealId;

    public PredictionRequest(int userId, String meal, int mealId) {
        this.userId = userId;
        this.meal = meal;
        this.mealId = mealId;
    }

    @Override
    public String toString() {
        return "PredictionRequest{" +
                "userId=" + userId +
                ", meal='" + meal + '\'' +
                ", mealId=" + mealId +
                '}';
    }
}
