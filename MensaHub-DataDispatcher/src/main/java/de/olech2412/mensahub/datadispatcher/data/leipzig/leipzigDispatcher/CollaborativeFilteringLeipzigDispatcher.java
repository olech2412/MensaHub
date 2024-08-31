package de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher;

import de.olech2412.mensahub.datadispatcher.email.Mailer;
import de.olech2412.mensahub.datadispatcher.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.datadispatcher.jpa.services.MailUserService;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.meals.MealsService;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringConfig;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringTags;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.api.APIError;
import de.olech2412.mensahub.models.result.errors.mail.MailError;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher.LeipzigDataDispatcher.getRecommendationScore;

@Service
@Log4j2
@Transactional
@EnableScheduling
public class CollaborativeFilteringLeipzigDispatcher {

    private final MealsService mealsService;
    private final MonitoringConfig monitoringConfig;
    private final ErrorEntityRepository errorEntityRepository;
    MailUserService mailUserService;
    @Autowired
    LeipzigDataDispatcher leipzigDataDispatcher;


    public CollaborativeFilteringLeipzigDispatcher(MailUserService mailUserService,
                                                   MealsService mealsService,
                                                   MonitoringConfig monitoringConfig,
                                                   ErrorEntityRepository errorEntityRepository) {
        this.mailUserService = mailUserService;
        this.mealsService = mealsService;
        this.monitoringConfig = monitoringConfig;
        this.errorEntityRepository = errorEntityRepository;
    }

    // schedule every 10 seconds
    @Scheduled(cron = "0/30 * * * * *")
    public void sendEmails() throws Exception {
        Counter mailCollabCounterSuccess = monitoringConfig.customCounter("mails_collab_success", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many collab mails were sent successfully");
        Counter mailCollabCounterFailure = monitoringConfig.customCounter("mails_collab_failure", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many collab mails were sent failure");
        Mailer mailer = new Mailer(mealsService);
        LocalDate today = LocalDate.now();
        LocalDate servingDate = today.plusDays(1);
        List<MailUser> mailUsers = mailUserService.findMailUserEnabledAndCollabInfoMailOnly().getData();
        if (mailUsers == null) {
            log.error("No mail users found");
            return;
        }
        for (MailUser mailUser : mailUsers) {
            // TODO: get the meals of the mensa where user subscribed to for next day
            Set<Mensa> subbedMensas = mailUser.getMensas();
            if (subbedMensas == null) {
                log.error("No mensas found for user {}", mailUser.getEmail());
                continue;
            }
            List<Meal> collaborativeFilteringMealCandidates = new ArrayList<>();

            for (Mensa mensa : subbedMensas) {
                List<Meal> meals = mealsService.findAllMealsByServingDateAndMensa(servingDate, mensa);
                if (meals == null) {
                    log.error("No meals found for mensa {}", mensa.getName());
                    continue;
                }
                collaborativeFilteringMealCandidates.addAll(meals);
            }
            if (collaborativeFilteringMealCandidates.isEmpty()) {
                log.error("No meals found for user {}, probably no meals on this date: {}", mailUser.getEmail(), servingDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                continue;
            }
            // TODO: get the PredictedRating rating of the user through collaborative filtering
            List<PredictionResult> predictionResults = getPredictionsForUserAndMeals(collaborativeFilteringMealCandidates, mailUser);
            if (predictionResults.isEmpty()) {
                log.error("No prediction results found for user {}", mailUser.getEmail());
                continue;
            }

            // Flag to determine if it should send an email
            boolean shouldSendEmail = false;

            for (PredictionResult predictionResult : predictionResults) {
                if (predictionResult.getPredictedRating() >= 3 && !predictionResult.getTrustScore().equals("niedrig")) {
                    shouldSendEmail = true;
                    break; // No need to check further once we find a valid rating
                }
            }

            if (shouldSendEmail) {
                Result<MailUser, MailError> mailResult = mailer.sendCollaborationMail(mailUser, predictionResults, servingDate);
                if (mailResult.isSuccess()) {
                    mailCollabCounterSuccess.increment();
                    log.info("Collab Info mail sent to {}", mailUser.getEmail());
                } else {
                    mailCollabCounterFailure.increment();
                    errorEntityRepository.save(new ErrorEntity(mailResult.getError().message(), mailResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                }
                // TODO: send push notification
                /*
                if (mailUser.isPushNotificationsEnabled()) {
                    sendPushNotification(buildMealMessage(mealsService.findAllMealsByServingDateAndMensa(today, mensa), mailUser),
                            "Speiseplan - " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + mensa.getName(),
                            mailUser, mensa);
                }
                 */
            }
        }
    }

    private List<PredictionResult> getPredictionsForUserAndMeals(List<Meal> collaborativeFilteringMealCandidates, MailUser mailUser) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        List<PredictionResult> predictionResults = new ArrayList<>();
        for (Meal meal : collaborativeFilteringMealCandidates) {
            Result<PredictionResult, APIError> predictionResultAPIErrorResult = getRecommendationScore(meal, mailUser);
            if (predictionResultAPIErrorResult.isSuccess()) {
                predictionResults.add(predictionResultAPIErrorResult.getData());
            } else {
                log.error("Prediction failed for meal {}. Error: {}", meal.getName(), predictionResultAPIErrorResult.getError());
            }
        }
        if (predictionResults.isEmpty()) {
            log.error("No prediction results found for user {}", mailUser.getEmail());
        }
        return predictionResults;
    }

    /* no used
    private Result<PredictionResult, APIError> getPredictionScore(Meal meal, MailUser mailUser) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        APIConfiguration apiConfiguration = new APIConfiguration();
        apiConfiguration.setBaseUrl(Config.getInstance().getProperty("mensaHub.junction.collaborative.filter.api.baseUrl"));
        CollaborativeFilteringAPIAdapter collaborativeFilteringAPIAdapter = new CollaborativeFilteringAPIAdapter(apiConfiguration);

        if (collaborativeFilteringAPIAdapter.isAPIAvailable()) {
            PredictionRequest predictionRequest = new PredictionRequest(Math.toIntExact(mailUser.getId()), meal.getName(), Math.toIntExact(meal.getId()));
            Result<List<Result<PredictionResult, APIError>>, APIError> predictionResults = collaborativeFilteringAPIAdapter.predict(List.of(predictionRequest));
            if (predictionResults.isSuccess()) {
                return predictionResults.getData().get(0);
            } else {
                log.error("Prediction failed for meal {}. Error: {}", meal.getName(), predictionResults.getError());
            }
        }
        return Result.error(new APIError("API not reachable", APIErrors.NETWORK_ERROR));
    }

     */

}
