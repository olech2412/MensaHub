package de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller.HTML_Caller;
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
import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.mail.MailError;
import de.olech2412.mensahub.models.result.errors.parser.ParserError;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@Transactional
@EnableScheduling
public class LeipzigDataDispatcher {

    @Autowired
    MailUserService mailUserService;

    @Autowired
    private MensasService mensasService;

    @Autowired
    private MealsService mealsService;

    @Autowired
    private MonitoringConfig monitoringConfig;

    @Autowired
    private ErrorEntityRepository errorEntityRepository;
    @Autowired
    private RatingService ratingService;


    public LeipzigDataDispatcher(
            MensasService mensasService,
            MealsService mealsService,
            MailUserService mailUserService,
            MonitoringConfig monitoringConfig) {

        this.mensasService = mensasService;
        this.mealsService = mealsService;
        this.mailUserService = mailUserService;
        this.monitoringConfig = monitoringConfig;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void callData() throws Exception {
        HTML_Caller dataCaller = new HTML_Caller(
                monitoringConfig.customCounter("stuwe_call_counter_success",
                        MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "How many calls were sent and parsed successfully"),
                monitoringConfig.customCounter("stuwe_call_counter_failure",
                        MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "How many call or parsing failure happened")
        );

        log.info("------------------ Data call for Leipzig ------------------");
        LocalDate currentDate = LocalDate.now();
        for (Mensa mensa : mensasService.findAll()) {
            String url = mensa.getApiUrl();
            int fetchDays = Integer.parseInt(Config.getInstance().getProperty("mensaHub.dataDispatcher.fetchDays"));
            for (int i = 0; i < fetchDays; i++) {
                LocalDate date = currentDate.plusDays(i);

                if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    url = url.replace("$date", date.toString());
                    log.info("Calling data for {} on {}", mensa.getName(), date);
                    Result<List<Meal>, ParserError> parsingResult = dataCaller.callDataFromStudentenwerk(url, mensa);
                    if (!parsingResult.isSuccess()) {
                        log.error("Error while calling data for {} on {}", mensa.getName(), date);
                        errorEntityRepository.save(new ErrorEntity(parsingResult.getError().message(), parsingResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                        return;
                    }
                    checkTheData(parsingResult.getData(), mensa);
                    url = url.replace(date.toString(), "$date");
                }
            }
        }

        log.info("------------------ Data call for Leipzig finished ------------------");
    }


    @Scheduled(cron = "0 00 08 ? * MON-FRI")
    public void sendEmails() {
        Counter mailCounterSuccess = monitoringConfig.customCounter("mails_success", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many mails were sent successfully");
        Counter mailCounterFailure = monitoringConfig.customCounter("mails_failure", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many mails were sent failure");
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();
        for (MailUser mailUser : mailUserService.findAll()) {
            if (mailUser.isEnabled()) {
                for (Mensa mensa : mailUser.getMensas()) {
                    Result<MailUser, MailError> mailResult = mailer.sendSpeiseplan(mailUser, mealsService.findAllMealsByServingDateAndMensa(today, mensa), mensa, false);
                    if (mailResult.isSuccess()) {
                        mailCounterSuccess.increment();
                        log.info("Regular mail sent to {} for mensa {}", mailUser.getEmail(), mensa.getName());
                    } else {
                        mailCounterFailure.increment();
                        errorEntityRepository.save(new ErrorEntity(mailResult.getError().message(), mailResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                    }
                }
            }
        }
    }

    public void checkTheData(List<Meal> data, Mensa mensa) {
        for (Meal newMeal : data) {
            List<Meal> databaseMeals = mealsService.findAllMealsByServingDateAndMensa(newMeal.getServingDate(), mensa);
            if (databaseMeals.isEmpty()) {
                mealsService.saveAll(data, mensa);
                log.info("Saved new meals: {}", data.size());
                return;
            } else {
                if (!databaseMeals.contains(newMeal)) {
                    for (Meal databaseMeal : databaseMeals) {
                        if (databaseMeal.getVotes() != 0) {
                            for (Meal newMeal1 : data) {
                                if (newMeal1.getName().equals(databaseMeal.getName())) {
                                    newMeal1.setVotes(databaseMeal.getVotes());
                                    newMeal1.setStarsTotal(databaseMeal.getStarsTotal());
                                    newMeal1.setRating(databaseMeal.getRating());
                                }
                            }
                        }
                    }
                    HashMap<Rating, Meal> deletedRatings = mealsService.deleteAllByServingDate(newMeal.getServingDate(), mensa);
                    mealsService.saveAll(data, mensa);
                    takeOldRatings(deletedRatings, data);
                    log.info("Meal updated: {}", newMeal);

                    if (newMeal.getServingDate().isEqual(LocalDate.now())) {
                        List<Result<MailUser, MailError>> sendResults = forceSendMail(mensa);
                        for (Result<MailUser, MailError> result : sendResults) {
                            if (result.isSuccess()) {
                                log.info("Successfully notified {} for update", result.getData().getEmail());
                            }
                        }
                    }
                }

            }
        }
    }

    private void takeOldRatings(HashMap<Rating, Meal> deletedRatings, List<Meal> newMeals) {
        for (Rating rating : deletedRatings.keySet()) {
            Optional<Meal> newMeal = newMeals.stream().filter(meal -> meal.getName().equals(rating.getMealName())).findFirst();
            if (newMeal.isPresent()) {
                rating.setMeal(newMeal.get());
                ratingService.saveRating(rating);
            } else {
                log.warn("Cannot find the new meal for a deleted rating {}. The deleted meal was {}. Rating will be deleted.", rating, deletedRatings.get(rating));
                ratingService.deleteRating(rating);
            }
        }
    }

    @Counted(value = "detected_updates", description = "How many updates were detected")
    public List<Result<MailUser, MailError>> forceSendMail(Mensa wantedMensa) {
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();
        Counter updateSentCounter = monitoringConfig.customCounter("updates_sent", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many updates were sent successfully");
        Counter updateSentCounterFailure = monitoringConfig.customCounter("updates_sent_failure", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many updates could not sent due to failures");
        List<Result<MailUser, MailError>> results = new ArrayList<>();
        for (Mensa mensa : mensasService.findAll()) {
            if (mensa.equals(wantedMensa)) {
                List<Meal> meals = mealsService.findAllMealsByServingDateAndMensa(today, mensa);
                List<MailUser> mailUsers = mailUserService.findAllByMensasAndEnabled(mensa, true);
                for (MailUser mailUser : mailUsers) {
                    if (mailUser.isWantsUpdate()) {
                        Result<MailUser, MailError> mailResult = mailer.sendSpeiseplan(mailUser, meals, mensa, true);
                        if (mailResult.isSuccess()) {
                            updateSentCounter.increment();
                            log.info("Update sent to {} for mensa {}", mailUser.getEmail(), mensa.getName());
                            results.add(mailResult);
                        } else {
                            updateSentCounterFailure.increment();
                            errorEntityRepository.save(new ErrorEntity(mailResult.getError().message(), mailResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                            results.add(mailResult);
                        }
                    }
                }
            }
        }
        return results;
    }

    @Counted(value = "detected_updates", description = "How many updates were detected")
    public List<Result<MailUser, MailError>> forceSendMail(List<MailUser> mailUsers, boolean isUpdate) {
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();
        Counter updateSentCounter = monitoringConfig.customCounter("updates_sent", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many updates were sent successfully");
        Counter updateSentCounterFailure = monitoringConfig.customCounter("updates_sent_failure", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many updates could not sent due to failures");
        List<Result<MailUser, MailError>> results = new ArrayList<>();
        for (MailUser mailUser : mailUsers) {
            if (mailUser.getMensas() == null) {
                mailUser.setMensas(mailUserService.findByEmail(mailUser.getEmail()).getMensas());
            }
            List<Mensa> mensen = mailUser.getMensas().stream().toList();
            for (Mensa mensa : mensen) {
                Result<MailUser, MailError> mailResult = mailer.sendSpeiseplan(mailUser, mealsService.findAllMealsByServingDateAndMensa(today, mensa), mensa, isUpdate);
                if (mailResult.isSuccess()) {
                    updateSentCounter.increment();
                    log.info("Update sent to {} for mensa {}", mailUser.getEmail(), mensa.getName());
                    results.add(mailResult);
                } else {
                    updateSentCounterFailure.increment();
                    errorEntityRepository.save(new ErrorEntity(mailResult.getError().message(), mailResult.getError().error().getCode(), Application.DATA_DISPATCHER));
                    results.add(mailResult);
                }
            }
        }
        return results;
    }
}