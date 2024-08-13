package de.olech2412.mensahub.junction.jpa.services;

import com.vaadin.flow.component.UI;
import de.olech2412.mensahub.APIConfiguration;
import de.olech2412.mensahub.CollaborativeFilteringAPIAdapter;
import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.gui.components.own.boxes.MealBox;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.models.addons.predictions.PredictionRequest;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.api.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MealPlanService {

    @Async
    public CompletableFuture<Void> addRecommendationScoreAsync(List<MealBox> mealBoxes, MailUser mailUser, UI ui) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (mailUser == null || mealBoxes == null || mealBoxes.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        APIConfiguration apiConfiguration = new APIConfiguration();
        apiConfiguration.setBaseUrl(Config.getInstance().getProperty("mensaHub.junction.collaborative.filter.api.baseUrl"));
        CollaborativeFilteringAPIAdapter collaborativeFilteringAPIAdapter = new CollaborativeFilteringAPIAdapter(apiConfiguration);

        if (collaborativeFilteringAPIAdapter.isAPIAvailable()) {
            List<PredictionRequest> predictionRequests = new ArrayList<>();
            for (MealBox mealBox : mealBoxes) {
                PredictionRequest predictionRequest = new PredictionRequest(mailUser.getId().intValue(), mealBox.getMealName(), mealBox.getMealId());
                predictionRequests.add(predictionRequest);
            }

            long startTime = System.currentTimeMillis();
            Result<List<Result<PredictionResult, APIError>>, APIError> predictionResults = collaborativeFilteringAPIAdapter.predict(predictionRequests);
            long endTime = System.currentTimeMillis();
            log.info("API Request to collaborative filter api with {} elements finished in {}ms", mealBoxes.size(), endTime - startTime);
            if (predictionResults.isSuccess()) {
                for (Result<PredictionResult, APIError> predictionResult : predictionResults.getData()) {
                    if (predictionResult.isSuccess()) {
                        Optional<MealBox> mealBoxOptional = mealBoxes.stream().filter(mealBox1 -> mealBox1.getMealName().equals(predictionResult.getData().getMealName())).findFirst();
                        if (mealBoxOptional.isPresent()) {
                            MealBox mealBox = mealBoxOptional.get();
                            ui.access(() -> mealBox.showRecommendation(predictionResult.getData()));
                        }
                    }
                }
                ui.access(ui::push); // Push UI updates to the client
            } else {
                log.error("Error while prediction results: {}. Error: {}", predictionResults, predictionResults.getError());
            }
        } else {
            log.error("Collaborative filtering API is not available");
            ui.access(() -> {
                NotificationFactory.create(NotificationType.WARN, "Aufgrund technischer Probleme können aktuell keine Empfehlungen angezeigt werden").open();
                ui.push(); // Push UI updates to the client
            });
        }

        return CompletableFuture.completedFuture(null);
    }
}
