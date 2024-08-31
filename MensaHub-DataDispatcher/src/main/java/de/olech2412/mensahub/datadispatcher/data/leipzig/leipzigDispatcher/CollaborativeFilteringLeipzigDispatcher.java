package de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher;

import de.olech2412.mensahub.datadispatcher.email.Mailer;
import de.olech2412.mensahub.datadispatcher.jpa.services.MailUserService;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.meals.MealsService;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringConfig;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringTags;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.api.APIError;
import de.olech2412.mensahub.models.result.errors.mail.MailError;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
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
import static de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher.LeipzigDataDispatcher.sendPushNotification;

@Service
@Log4j2
@Transactional
@EnableScheduling
public class CollaborativeFilteringLeipzigDispatcher {

    private final MealsService mealsService;
    private final MonitoringConfig monitoringConfig;
    MailUserService mailUserService;


    public CollaborativeFilteringLeipzigDispatcher(MailUserService mailUserService,
                                                   MealsService mealsService,
                                                   MonitoringConfig monitoringConfig
    ) {
        this.mailUserService = mailUserService;
        this.mealsService = mealsService;
        this.monitoringConfig = monitoringConfig;
    }

    /**
     * Sends emails to users who want to receive collaborative filtering mails
     *
     * @throws Exception If sending the emails fails
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void sendEmails() throws Exception {
        Counter mailCollabCounterSuccess = monitoringConfig.customCounter("mails_collab_success", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many collab mails were sent successfully");
        Counter mailCollabCounterFailure = monitoringConfig.customCounter("mails_collab_failure", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many collab mails were sent failure");
        Mailer mailer = new Mailer(mealsService);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<MailUser> mailUsers = mailUserService.findMailUsersByWantsCollaborationInfoMailIsTrue().getData();
        if (mailUsers == null) {
            log.error("No mail users found");
            return;
        }
        for (MailUser mailUser : mailUsers) {

            Set<Mensa> subbedMensas = mailUser.getMensas();
            if (subbedMensas == null) {
                log.error("No mensas found for user {}, continuing with the next user", mailUser.getEmail());
                continue;
            }

            for (Mensa mensa : subbedMensas) {
                List<Meal> collaborativeFilteringMealCandidates = mealsService.findAllMealsByServingDateAndMensa(tomorrow, mensa);
                if (collaborativeFilteringMealCandidates.isEmpty()) {
                    log.error("No meals found for mensa {}", mensa.getName());
                } else {
                    List<PredictionResult> predictionResults = getPredictionsForUserAndMeals(collaborativeFilteringMealCandidates, mailUser);
                    if (predictionResults.isEmpty()) {
                        log.error("No prediction results found for user {}", mailUser.getEmail());
                        continue;
                    }

                    // Flag to determine if it should send an email
                    boolean shouldSendInfos = false;

                    for (PredictionResult predictionResult : predictionResults) {
                        if (predictionResult.getPredictedRating() >= 3 && !predictionResult.getTrustScore().equals("niedrig")) {
                            shouldSendInfos = true;
                            break; // No need to check further once we find a valid rating
                        }
                    }

                    if (shouldSendInfos) {

                        // wenn user enabled ist, bekommt er auf jeden Fall eine Mail, wenn nicht, dann nicht
                        // wenn er zusätzlich push notifications enabled hat, dann bekommt er auch eine push notification, wenn nicht, dann gar nichts, dann nächster User
                        // wenn mails ending !success, dann error increment
                        if (mailUser.isEnabled()) {
                            Result<MailUser, MailError> mailUserMailResult = mailer.sendCollaborationMail(mailUser, predictionResults, mensa, tomorrow);
                            if (mailUserMailResult.isSuccess()) {
                                mailCollabCounterSuccess.increment();
                                log.info("Mail sent successfully to user {}", mailUser.getEmail());
                            } else {
                                log.error("Mail sending failed for user {}. Error: {}", mailUser.getEmail(), mailUserMailResult.getError());
                                mailCollabCounterFailure.increment();
                            }
                            sendCollabPushNotification(tomorrow, mailUser, mensa);
                        } else {
                            sendCollabPushNotification(tomorrow, mailUser, mensa);
                        }
                    }

                }
            }
        }
    }

    /**
     * Sends a push notification to a user
     *
     * @param mailCollabCounterFailure The counter to increment if sending the push notification fails
     * @param tomorrow                 The date for the push notification
     * @param mailUser                 The user to send the push notification to
     * @param mensa                    The mensa to send the push notification for
     */
    private void sendCollabPushNotification(LocalDate tomorrow, MailUser mailUser, Mensa mensa) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (mailUser.isPushNotificationsEnabled()) {
            String message = buildMealMessage(mailUser, mensa, tomorrow);
            sendPushNotification(message, "Empfehlungen für morgen, den " + tomorrow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), mailUser, mensa, tomorrow);
        }
    }

    /**
     * Builds the message for the mail
     *
     * @param mailUser The user to build the message for
     * @param mensa    The mensa to build the message for
     * @param tomorrow The date to build the message for
     * @return The message
     */
    private String buildMealMessage(MailUser mailUser, Mensa mensa, LocalDate tomorrow) {
        String message = "Hallo " + mailUser.getFirstname() + ",\n" +
                "klicke hier für deine Empfehlungen für morgen, den " + tomorrow + " in der " + mensa.getName() + "\n" +
                "Guten Appetit!";
        return message;

    }

    /**
     * Gets the predictions for a user and a list of meals
     *
     * @param collaborativeFilteringMealCandidates The meals to get predictions for
     * @param mailUser                             The user to get predictions for
     * @return A list of prediction results for the user and the meals
     */
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
        return predictionResults;
    }

}
