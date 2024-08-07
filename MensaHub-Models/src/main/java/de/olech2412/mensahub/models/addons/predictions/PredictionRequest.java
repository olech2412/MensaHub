package de.olech2412.mensahub.models.addons.predictions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionRequest {
    private int userId;
    private String meal;

    public PredictionRequest(int userId, String meal) {
        this.userId = userId;
        this.meal = meal;
    }

    @Override
    public String toString() {
        return "PredictionRequest{" +
                "userId=" + userId +
                ", meal='" + meal + '\'' +
                '}';
    }
}
