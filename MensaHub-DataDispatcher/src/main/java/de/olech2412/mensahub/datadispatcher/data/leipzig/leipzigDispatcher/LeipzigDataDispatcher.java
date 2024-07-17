package de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller.HTML_Caller;
import de.olech2412.mensahub.datadispatcher.email.Mailer;
import de.olech2412.mensahub.datadispatcher.jpa.services.MailUserService;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.meals.MealsService;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.mensen.MensasService;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringConfig;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringTags;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

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
    @Transactional
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
                    checkTheData(dataCaller.callDataFromStudentenwerk(url, mensa), mensa);
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
            try {
                if (mailUser.isEnabled()) {
                    for (Mensa mensa : mailUser.getMensas()) {
                        mailer.sendSpeiseplan(mailUser, mealsService.findAllMealsByServingDateAndMensa(today, mensa), mensa, false);
                        log.info("Sent speiseplan for user: {} for mensa: {}", mailUser.getEmail(), mensa.getName());
                        mailCounterSuccess.increment();
                    }
                }
            } catch (Exception exception) {
                log.error("Error while sending email to {}: {}", mailUser.getEmail(), exception.getMessage());
                mailCounterFailure.increment();
            }
        }
    }

    @Transactional
    public void checkTheData(List<Meal> data, Mensa mensa) throws Exception {
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
                    mealsService.deleteAllByServingDate(newMeal.getServingDate(), mensa);

                    mealsService.saveAll(data, mensa);

                    if (newMeal.getServingDate().isEqual(LocalDate.now())) sendUpdate(mensa);

                    log.info("Meal updated: {}", newMeal);
                }

            }
        }
    }

    @Counted(value = "detected_updates", description = "How many updates were detected")
    protected void sendUpdate(Mensa wantedMensa) throws Exception {
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();
        Counter updateSentCounter = monitoringConfig.customCounter("updates_sent", MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(),
                "How many updates were sent successfully");
        for (Mensa mensa : mensasService.findAll()) {
            if (mensa.equals(wantedMensa)) {
                List<Meal> meals = mealsService.findAllMealsByServingDateAndMensa(today, mensa);
                List<MailUser> mailUsers = mailUserService.findAllByMensasAndEnabled(mensa, true);
                for (MailUser mailUser : mailUsers) {
                    if (mailUser.isWantsUpdate()) {
                        mailer.sendSpeiseplan(mailUser, meals, mensa, true);
                        log.info("Update sent to {} for mensa {}", mailUser.getEmail(), mensa.getName());
                        updateSentCounter.increment();
                    }
                }
            }
        }

    }

    protected Meal takeOldVotes(MealsService mealsService, Meal meal, Mensa mensa) {
        List<Meal> meals = mealsService.findAllByNameAndMensaAndServingDateBeforeOrderByServingDateDesc(meal.getName(), mensa, LocalDate.now());
        if (!meals.isEmpty()) {
            meal.setRating(meals.get(0).getRating());
            meal.setVotes(meals.get(0).getVotes());
            meal.setStarsTotal(meals.get(0).getStarsTotal());
            log.info("Old votes taken for meal: " + meal.getName());
        }
        return meal;
    }
}