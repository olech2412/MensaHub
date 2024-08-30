package de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher;

import de.olech2412.mensahub.APIConfiguration;
import de.olech2412.mensahub.CollaborativeFilteringAPIAdapter;
import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.email.Mailer;
import de.olech2412.mensahub.datadispatcher.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.datadispatcher.jpa.services.MailUserService;
import de.olech2412.mensahub.datadispatcher.jpa.services.RatingService;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.meals.MealsService;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.mensen.MensasService;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringConfig;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringTags;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.addons.predictions.PredictionRequest;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.api.APIError;
import de.olech2412.mensahub.models.result.errors.api.APIErrors;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
@Transactional
@EnableScheduling
public class CollaborativeFilteringLeipzigDispatcher {

    private final MensasService mensasService;
    private final MealsService mealsService;
    private final MonitoringConfig monitoringConfig;
    private final ErrorEntityRepository errorEntityRepository;
    private final RatingService ratingService;
    MailUserService mailUserService;
    @Autowired
    LeipzigDataDispatcher leipzigDataDispatcher;


    public CollaborativeFilteringLeipzigDispatcher(MailUserService mailUserService,
                                                   MensasService mensasService,
                                                   MealsService mealsService,
                                                   MonitoringConfig monitoringConfig,
                                                   ErrorEntityRepository errorEntityRepository,
                                                   RatingService ratingService) {
        this.mailUserService = mailUserService;
        this.mensasService = mensasService;
        this.mealsService = mealsService;
        this.monitoringConfig = monitoringConfig;
        this.errorEntityRepository = errorEntityRepository;
        this.ratingService = ratingService;
    }

    // schedule every 10 seconds
    @Scheduled(cron = "0/20 * * * * *")
    public void sendEmails() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Counter mailCollabCounterSuccess = monitoringConfig.customCounter("mails_collab_success", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many collab mails were sent successfully");
        Counter mailCollabCounterFailure = monitoringConfig.customCounter("mails_collab_failure", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many collab mails were sent failure");
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(3);
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
                List<Meal> meals = mealsService.findAllMealsByServingDateAndMensa(tomorrow, mensa);
                if (meals == null) {
                    log.error("No meals found for mensa {}", mensa.getName());
                    continue;
                }
                collaborativeFilteringMealCandidates.addAll(meals);
            }
            // TODO: get the PredictedRating rating of the user through collaborative filtering
            List<PredictionResult> predictionResults = getPredictionsForUserAndMeals(collaborativeFilteringMealCandidates, mailUser);
            if (predictionResults.isEmpty()) {
                log.error("No prediction results found for user {}", mailUser.getEmail());
                continue;
            }

            // TODO: if at least one meal PredictedRating for liking is equal or greater than 3 then create email text based on predictionresultlist
            for (PredictionResult predictionResult : predictionResults) {
                if (predictionResult.getPredictedRating() >= 3) {
                    //         public Result<MailUser, MailError> sendCollaborationMail(MailUser emailTarget, List<Meal> predictions, Mensa mensa) {
                    LocalDate servingDateOfMeal = mealsService.findMealById((long) predictionResult.getMealId()).getServingDate();
                    mailer.sendCollaborationMail(mailUser, predictionResults, servingDateOfMeal);
                } else {
                    log.info("No meal with rating greater or equal to 3 found for user {}", mailUser.getEmail());
                }
            }

        }

            /*
            if (mailUser.isEnabled()) {
                for (Mensa mensa : mailUser.getMensas()) {
                    Result<MailUser, MailError> mailResult = mailer.sendSpeiseplan(mailUser, mealsService.findAllMealsByServingDateAndMensa(today, mensa), mensa, false);
                    if (mailResult.isSuccess()) {
                        mailCollabCounterSuccess.increment();
                        log.info("Regular mail sent to {} for mensa {}", mailUser.getEmail(), mensa.getName());
                    } else {
                        mailCollabCounterFailure.increment();
                        errorEntityRepository.save(new ErrorEntity(mailResult.getError().message(), mailResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                    }
                    if (mailUser.isPushNotificationsEnabled()) {
                        sendPushNotification(buildMealMessage(mealsService.findAllMealsByServingDateAndMensa(today, mensa), mailUser),
                                "Speiseplan - " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + mensa.getName(),
                                mailUser, mensa);
                    }
                }
            } else {
                for (Mensa mensa : mailUser.getMensas()) {
                    Result<MailUser, MailError> mailResult = mailer.sendSpeiseplan(mailUser, mealsService.findAllMealsByServingDateAndMensa(today, mensa), mensa, false);
                    if (mailResult.isSuccess()) {
                        mailCounterSuccess.increment();
                        log.info("Regular mail sent to {} for mensa {}", mailUser.getEmail(), mensa.getName());
                    } else {
                        mailCounterFailure.increment();
                        errorEntityRepository.save(new ErrorEntity(mailResult.getError().message(), mailResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                    }
                    if (mailUser.isPushNotificationsEnabled()) {
                        sendPushNotification(buildMealMessage(mealsService.findAllMealsByServingDateAndMensa(today, mensa), mailUser),
                                "Speiseplan - " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + mensa.getName(),
                                mailUser, mensa);
                    }
                }
            }
        }*/
    }

    private List<PredictionResult> getPredictionsForUserAndMeals(List<Meal> collaborativeFilteringMealCandidates, MailUser mailUser) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        StringBuilder mealMessage = new StringBuilder();
        List<PredictionResult> predictionResults = new ArrayList<>();
        for (Meal meal : collaborativeFilteringMealCandidates) {
            Result<PredictionResult, APIError> predictionResultAPIErrorResult = getPredictionScore(meal, mailUser);
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

}
