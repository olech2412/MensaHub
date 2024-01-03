package de.olech2412.mensahub.datadispatcher.Data.Leipzig.LeipzigDispatcher;

import de.olech2412.mensahub.datadispatcher.Data.Leipzig.html_caller.HTML_Caller;
import de.olech2412.mensahub.datadispatcher.JPA.repository.Leipzig.AllergeneRepository;
import de.olech2412.mensahub.datadispatcher.JPA.services.Leipzig.meals.*;
import de.olech2412.mensahub.datadispatcher.JPA.services.Leipzig.mensen.*;
import de.olech2412.mensahub.datadispatcher.JPA.services.MailUserService;
import de.olech2412.mensahub.datadispatcher.email.Mailer;
import de.olech2412.mensahub.models.Leipzig.Allergene;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@Transactional
@EnableScheduling
public class LeipzigDataDispatcher {

    @Autowired
    private Meals_Mensa_Schoenauer_StrService meals_mensa_schoenauer_strService;
    @Autowired
    private Meals_Mensa_am_MedizincampusService meals_mensa_am_medizincampusService;
    @Autowired
    private Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService;
    @Autowired
    private Meals_Mensa_AcademicaService meals_mensa_academicaService;
    @Autowired
    private Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService;
    @Autowired
    private Meals_Mensa_PeterssteinwegService meals_mensa_peterssteinwegService;
    @Autowired
    private Meals_Mensa_am_ParkService meals_mensa_am_parkService;
    @Autowired
    private Meals_Mensa_TierklinikService meals_mensa_tierklinikService;
    @Autowired
    private Meals_Menseria_am_Botanischen_GartenServices meals_menseria_am_botanischen_gartenServices;

    @Autowired
    private Mensa_Schoenauer_StrService mensa_schoenauer_strService;
    @Autowired
    private Mensa_am_MedizincampusService mensa_am_medizincampusService;
    @Autowired
    private Mensa_AcademicaService mensa_academicaService;
    @Autowired
    private Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService;
    @Autowired
    private Mensa_PeterssteinwegService mensa_peterssteinwegService;
    @Autowired
    private Mensa_am_ParkService mensa_am_parkService;
    @Autowired
    private Mensa_TierklinikService mensa_tierklinikService;
    @Autowired
    private Cafeteria_DittrichringService cafeteria_dittrichringService;
    @Autowired
    private Menseria_am_Botanischen_GartenService menseria_am_botanischen_gartenService;

    @Autowired
    private AllergeneRepository allergeneRepository;

    @Autowired
    MailUserService mailUserService;

    HashMap<Mensa_Service, Meals_Mensa_Service> mensa_meals_serviceHashMap = new HashMap<>();
    List<Mensa_Service> mensa_serviceList = new ArrayList<>();

    LocalTime startTime = LocalTime.of(8, 6);
    LocalTime endTime = LocalTime.of(16, 0);


    public LeipzigDataDispatcher(
            Meals_Mensa_Schoenauer_StrService meals_mensa_schoenauer_strService,
            Meals_Mensa_am_MedizincampusService meals_mensa_am_medizincampusService,
            Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService,
            Meals_Mensa_AcademicaService meals_mensa_academicaService,
            Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService,
            Meals_Mensa_PeterssteinwegService meals_mensa_peterssteinwegService,
            Meals_Mensa_am_ParkService meals_mensa_am_parkService,
            Meals_Mensa_TierklinikService meals_mensa_tierklinikService,
            Meals_Menseria_am_Botanischen_GartenServices meals_menseria_am_botanischen_gartenServices,
            Mensa_Schoenauer_StrService mensa_schoenauer_strService,
            Mensa_am_MedizincampusService mensa_am_medizincampusService,
            Mensa_AcademicaService mensa_academicaService,
            Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService,
            Mensa_PeterssteinwegService mensa_peterssteinwegService,
            Mensa_am_ParkService mensa_am_parkService,
            Mensa_TierklinikService mensa_tierklinikService,
            Cafeteria_DittrichringService cafeteria_dittrichringService,
            Menseria_am_Botanischen_GartenService menseria_am_botanischen_gartenService,
            AllergeneRepository allergeneRepository,
            MailUserService mailUserService) {

        this.meals_mensa_schoenauer_strService = meals_mensa_schoenauer_strService;
        this.meals_mensa_am_medizincampusService = meals_mensa_am_medizincampusService;
        this.meals_cafeteria_dittrichringService = meals_cafeteria_dittrichringService;
        this.meals_mensa_academicaService = meals_mensa_academicaService;
        this.meals_mensa_am_elsterbeckenService = meals_mensa_am_elsterbeckenService;
        this.meals_mensa_peterssteinwegService = meals_mensa_peterssteinwegService;
        this.meals_mensa_am_parkService = meals_mensa_am_parkService;
        this.meals_mensa_tierklinikService = meals_mensa_tierklinikService;
        this.meals_menseria_am_botanischen_gartenServices = meals_menseria_am_botanischen_gartenServices;
        this.mensa_schoenauer_strService = mensa_schoenauer_strService;
        this.mensa_am_medizincampusService = mensa_am_medizincampusService;
        this.mensa_academicaService = mensa_academicaService;
        this.mensa_am_elsterbeckenService = mensa_am_elsterbeckenService;
        this.mensa_peterssteinwegService = mensa_peterssteinwegService;
        this.mensa_am_parkService = mensa_am_parkService;
        this.mensa_tierklinikService = mensa_tierklinikService;
        this.cafeteria_dittrichringService = cafeteria_dittrichringService;
        this.menseria_am_botanischen_gartenService = menseria_am_botanischen_gartenService;
        this.allergeneRepository = allergeneRepository;
        this.mailUserService = mailUserService;

        configureHashMap();
        prepareMensaList();
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void callData() throws IOException, MessagingException {
        Timer.Sample timerSample = Timer.start();
        HTML_Caller dataCaller = new HTML_Caller();
        log.info("------------------ Data call for Leipzig ------------------");
        LocalDate currentDate = LocalDate.now();
        for (Mensa_Service mensa_service : mensa_serviceList) {
            String url = mensa_service.getMensa().getApiUrl();

            for (int i = 0; i < 14; i++) {
                LocalDate date = currentDate.plusDays(i);

                if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    url = url.replace("$date", date.toString());
                    Meals_Mensa_Service meals_mensa_service = mensa_meals_serviceHashMap.get(mensa_service);
                    log.info("Calling data for " + mensa_service.getMensa().getName() + " on " + date);
                    checkTheData(dataCaller.callDataFromStudentenwerk(url), mensa_service, mensa_meals_serviceHashMap);
                    url = url.replace(date.toString(), "$date");
                }
            }
        }

        log.info("------------------ Data call for Leipzig finished ------------------");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void fetchAllergenes() throws IOException {
        HTML_Caller dataCaller = new HTML_Caller();
        Map<String, String> dataMap = dataCaller.fetchAllergensandAdditives(mensa_serviceList.get(0).getMensa().getApiUrl());
        insertNewAllergenes(dataMap);
    }

    protected void insertNewAllergenes(Map<String, String> dataMap) {
        allergeneRepository.deleteAll();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                Allergene allergene = new Allergene();
                allergene.setAllergen(entry.getValue());
                allergene.setToken(entry.getKey());
                allergeneRepository.save(allergene);
            }
    }


    @Scheduled(cron = "0 0 * * * *")
    protected void dailyDataCheck() {
        log.info("------------------ Data check for Leipzig ------------------");
        log.info("---------Check data for Mensa Schoenauer Str---------");
        checkDataValidity(meals_mensa_schoenauer_strService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_schoenauer_strService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa am Medizincampus---------");
        checkDataValidity(meals_mensa_am_medizincampusService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_am_medizincampusService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Cafeteria Dittrichring---------");
        checkDataValidity(meals_cafeteria_dittrichringService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), cafeteria_dittrichringService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa Academica---------");
        checkDataValidity(meals_mensa_academicaService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_academicaService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa am Elsterbecken---------");
        checkDataValidity(meals_mensa_am_elsterbeckenService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_am_elsterbeckenService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa Peterssteinweg---------");
        checkDataValidity(meals_mensa_peterssteinwegService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_peterssteinwegService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa am Park---------");
        checkDataValidity(meals_mensa_am_parkService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_am_parkService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa Tierklinik---------");
        checkDataValidity(meals_mensa_tierklinikService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), mensa_tierklinikService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Menseria am Botanischen Garten---------");
        checkDataValidity(meals_menseria_am_botanischen_gartenServices.findAllMealsByServingDateGreaterThanEqual(LocalDate.now()), menseria_am_botanischen_gartenService, mensa_meals_serviceHashMap);
        log.info("------------------ Data check for Leipzig finished ------------------");
    }

    @Scheduled(cron = "0 00 03 ? * WED")
    public void weeklyDataCheck() {
        log.info("------------------ Weekly data check for Leipzig ------------------");
        log.info("---------Check data for Mensa Schoenauer Str---------");
        checkDataValidity(meals_mensa_schoenauer_strService.findAll(), mensa_schoenauer_strService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa am Medizincampus---------");
        checkDataValidity(meals_mensa_am_medizincampusService.findAll(), mensa_am_medizincampusService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Cafeteria Dittrichring---------");
        checkDataValidity(meals_cafeteria_dittrichringService.findAll(), cafeteria_dittrichringService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa Academica---------");
        checkDataValidity(meals_mensa_academicaService.findAll(), mensa_academicaService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa am Elsterbecken---------");
        checkDataValidity(meals_mensa_am_elsterbeckenService.findAll(), mensa_am_elsterbeckenService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa Peterssteinweg---------");
        checkDataValidity(meals_mensa_peterssteinwegService.findAll(), mensa_peterssteinwegService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa am Park---------");
        checkDataValidity(meals_mensa_am_parkService.findAll(), mensa_am_parkService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Mensa Tierklinik---------");
        checkDataValidity(meals_mensa_tierklinikService.findAll(), mensa_tierklinikService, mensa_meals_serviceHashMap);
        log.info("---------Check data for Menseria am Botanischen Garten---------");
        checkDataValidity(meals_menseria_am_botanischen_gartenServices.findAll(), menseria_am_botanischen_gartenService, mensa_meals_serviceHashMap);
        log.info("------------------ Weekly data check for Leipzig finished ------------------");
    }

    public void checkDataValidity(Iterable<? extends Meal> all, Mensa_Service mensa_service, HashMap<Mensa_Service, Meals_Mensa_Service> mensa_meals_serviceHashMap) {
        Meals_Mensa_Service meals_mensa_service = mensa_meals_serviceHashMap.get(mensa_service);
        boolean valid = true;
        int counter = 0;
        for (Meal meal : all) {
            if (meal.getAdditives() == null || meal.getAdditives().isEmpty()) {
                log.error("Meal " + meal.getName() + " has no additives. Set to N/A. ❌");
                meal.setAdditives("N/A");
                valid = false;
                counter++;
                meals_mensa_service.save(meal, mensa_service.getMensa());
            }
            if (meal.getCategory() == null || meal.getCategory().isEmpty()) {
                log.error("Meal " + meal.getName() + " has no category. Set to N/A. ❌");
                meal.setCategory("N/A");
                valid = false;
                counter++;
                meals_mensa_service.save(meal, mensa_service.getMensa());
            }
            if (meal.getAllergens() == null || meal.getAllergens().isEmpty()) {
                log.error("Meal " + meal.getName() + " has no allergens. Set to N/A. ❌");
                meal.setAllergens("N/A");
                valid = false;
                counter++;
                meals_mensa_service.save(meal, mensa_service.getMensa());
            }
            if (meal.getDescription() == null || meal.getDescription().isEmpty()) {
                log.error("Meal " + meal.getName() + " has no description. Set to N/A. ❌");
                meal.setDescription("N/A");
                valid = false;
                counter++;
                meals_mensa_service.save(meal, mensa_service.getMensa());
            }
            if (meal.getAdditionalInfo() == null || meal.getAdditionalInfo().isEmpty()) {
                log.error("Meal " + meal.getName() + " has no additionalInfo. Set to N/A. ❌");
                meal.setAdditionalInfo("N/A");
                valid = false;
                counter++;
                meals_mensa_service.save(meal, mensa_service.getMensa());
            }
            if (meal.getPrice() == null || meal.getPrice().isEmpty()) {
                log.error("Meal " + meal.getName() + " has no price. Set to N/A. ❌");
                meal.setPrice("N/A");
                valid = false;
                counter++;
                meals_mensa_service.save(meal, mensa_service.getMensa());
            }
        }
        if (valid) {
            log.info("All meals are valid. ✅");
        } else {
            log.error(counter + " meals were invalid. ❌");
        }
    }

    @Scheduled(cron = "0 00 08 ? * MON-FRI")
    public void sendEmails() throws MessagingException, IOException {
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();
        for (MailUser mailUser : mailUserService.findAll()) {
            if (mailUser.isEnabled()) {
                if (mailUser.getCafeteria_dittrichring() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_cafeteria_dittrichringService.findAllMealsByServingDate(today), cafeteria_dittrichringService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for cafeteria_dittrichring");
                }
                if (mailUser.getMensa_academica() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_academicaService.findAllMealsByServingDate(today), mensa_academicaService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_academica");
                }
                if (mailUser.getMensa_am_elsterbecken() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_am_elsterbeckenService.findAllMealsByServingDate(today), mensa_am_elsterbeckenService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_am_elsterbecken");
                }
                if (mailUser.getMensa_am_medizincampus() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_am_medizincampusService.findAllMealsByServingDate(today), mensa_am_medizincampusService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_am_medizincampus");
                }
                if (mailUser.getMensa_am_park() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_am_parkService.findAllMealsByServingDate(today), mensa_am_parkService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_am_park");
                }
                if (mailUser.getMensa_peterssteinweg() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_peterssteinwegService.findAllMealsByServingDate(today), mensa_peterssteinwegService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_peterssteinweg");
                }
                if (mailUser.getMensa_schoenauer_str() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_schoenauer_strService.findAllMealsByServingDate(today), mensa_schoenauer_strService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_schoenauer_str");
                }
                if (mailUser.getMensa_tierklinik() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_mensa_tierklinikService.findAllMealsByServingDate(today), mensa_tierklinikService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for mensa_tierklinik");
                }
                if (mailUser.getMenseria_am_botanischen_garten() != null) {
                    mailer.sendSpeiseplan(mailUser, meals_menseria_am_botanischen_gartenServices.findAllMealsByServingDate(today), menseria_am_botanischen_gartenService.getMensa(), allergeneRepository.findAll(), false);
                    log.info("Email sent to " + mailUser.getEmail() + " for menseria_am_botanischen_garten");
                }
            }
        }
    }

    protected void prepareMensaList() {
        mensa_serviceList.add(mensa_schoenauer_strService);
        mensa_serviceList.add(cafeteria_dittrichringService);
        mensa_serviceList.add(mensa_academicaService);
        mensa_serviceList.add(mensa_am_elsterbeckenService);
        mensa_serviceList.add(mensa_am_medizincampusService);
        mensa_serviceList.add(mensa_am_parkService);
        mensa_serviceList.add(mensa_peterssteinwegService);
        mensa_serviceList.add(mensa_tierklinikService);
        mensa_serviceList.add(menseria_am_botanischen_gartenService);
    }

    protected void configureHashMap() {
        mensa_meals_serviceHashMap.put(mensa_schoenauer_strService, meals_mensa_schoenauer_strService);
        mensa_meals_serviceHashMap.put(cafeteria_dittrichringService, meals_cafeteria_dittrichringService);
        mensa_meals_serviceHashMap.put(mensa_academicaService, meals_mensa_academicaService);
        mensa_meals_serviceHashMap.put(mensa_am_elsterbeckenService, meals_mensa_am_elsterbeckenService);
        mensa_meals_serviceHashMap.put(mensa_am_medizincampusService, meals_mensa_am_medizincampusService);
        mensa_meals_serviceHashMap.put(mensa_am_parkService, meals_mensa_am_parkService);
        mensa_meals_serviceHashMap.put(mensa_peterssteinwegService, meals_mensa_peterssteinwegService);
        mensa_meals_serviceHashMap.put(mensa_tierklinikService, meals_mensa_tierklinikService);
        mensa_meals_serviceHashMap.put(menseria_am_botanischen_gartenService, meals_menseria_am_botanischen_gartenServices);
    }

    protected void checkTheData(List<Meal> data, Mensa_Service mensa_service, HashMap<Mensa_Service, Meals_Mensa_Service> mensa_meals_serviceHashMap) throws MessagingException, IOException {
        Meals_Mensa_Service meals_mensa_service = mensa_meals_serviceHashMap.get(mensa_service);
        for (Meal newMeal : data) {
            List<? extends Meal> databaseMeals = meals_mensa_service.findAllMealsByServingDate(newMeal.getServingDate());
            if(databaseMeals.isEmpty()) {
                meals_mensa_service.save(newMeal, mensa_service.getMensa());
                log.info("New meal saved: " + newMeal);
            } else {
                if(!databaseMeals.contains(newMeal) || databaseMeals.size() != data.size()) {
                    for (Meal databaseMeal : databaseMeals) {
                        if(databaseMeal.getVotes() != 0){
                            for (Meal newMeal1 : data) {
                                if(newMeal1.getName().equals(databaseMeal.getName())){
                                    newMeal1.setVotes(databaseMeal.getVotes());
                                    newMeal1.setStarsTotal(databaseMeal.getStarsTotal());
                                    newMeal1.setRating(databaseMeal.getRating());
                                }
                            }
                        }
                    }
                    meals_mensa_service.deleteAllByServingDate(newMeal.getServingDate());
                    meals_mensa_service.saveAll(data, mensa_service.getMensa());

                   if(newMeal.getServingDate().isEqual(LocalDate.now())) sendUpdate(mensa_service.getMensa());

                    log.info("Meal updated: " + newMeal);
                }
            }
        }
    }

    protected void sendUpdate(Mensa mensa) throws MessagingException, IOException {
        Mailer mailer = new Mailer();
        LocalDate today = LocalDate.now();

        for (Mensa_Service mensa_service : mensa_serviceList) {
            if (mensa_service.getMensa().equals(mensa)) {
                Meals_Mensa_Service meals_mensa_service = mensa_meals_serviceHashMap.get(mensa_service);
                List<? extends Meal> meals = meals_mensa_service.findAllMealsByServingDate(today);
                Iterable<MailUser> mailUsers = mailUserService.findAllUsersThatAreEnabled();
                for (MailUser mailUser : mailUsers) {
                    if (mailUser.getCafeteria_dittrichring() != null && mailUser.getCafeteria_dittrichring().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_cafeteria_dittrichringService.findAllMealsByServingDate(today), cafeteria_dittrichringService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for cafeteria_dittrichring");
                    }
                    if (mailUser.getMensa_academica() != null && mailUser.getMensa_academica().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_academicaService.findAllMealsByServingDate(today), mensa_academicaService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_academica");
                    }
                    if (mailUser.getMensa_am_elsterbecken() != null && mailUser.getMensa_am_elsterbecken().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_am_elsterbeckenService.findAllMealsByServingDate(today), mensa_am_elsterbeckenService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_am_elsterbecken");
                    }
                    if (mailUser.getMensa_am_medizincampus() != null && mailUser.getMensa_am_medizincampus().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_am_medizincampusService.findAllMealsByServingDate(today), mensa_am_medizincampusService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_am_medizincampus");
                    }
                    if (mailUser.getMensa_am_park() != null && mailUser.getMensa_am_park().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_am_parkService.findAllMealsByServingDate(today), mensa_am_parkService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_am_park");
                    }
                    if (mailUser.getMensa_peterssteinweg() != null && mailUser.getMensa_peterssteinweg().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_peterssteinwegService.findAllMealsByServingDate(today), mensa_peterssteinwegService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_peterssteinweg");
                    }
                    if (mailUser.getMensa_schoenauer_str() != null && mailUser.getMensa_schoenauer_str().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_schoenauer_strService.findAllMealsByServingDate(today), mensa_schoenauer_strService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_schoenauer_str");
                    }
                    if (mailUser.getMensa_tierklinik() != null && mailUser.getMensa_tierklinik().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_mensa_tierklinikService.findAllMealsByServingDate(today), mensa_tierklinikService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for mensa_tierklinik");
                    }
                    if (mailUser.getMenseria_am_botanischen_garten() != null && mailUser.getMenseria_am_botanischen_garten().equals(mensa)) {
                        mailer.sendSpeiseplan(mailUser, meals_menseria_am_botanischen_gartenServices.findAllMealsByServingDate(today), menseria_am_botanischen_gartenService.getMensa(), allergeneRepository.findAll(), true);
                        log.info("Update sent to " + mailUser.getEmail() + " for menseria_am_botanischen_garten");
                    }
                }
            }

        }
    }

    protected Meal takeOldVotes(Meals_Mensa_Service meals_mensa_service, Meal meal) {
        List<? extends Meal> meals = meals_mensa_service.findMealsFromMensaByNameAndServingDateBeforeOrderByServingDateDesc(meal.getName(), LocalDate.now());
        if (!meals.isEmpty()) {
            meal.setRating(meals.get(0).getRating());
            meal.setVotes(meals.get(0).getVotes());
            meal.setStarsTotal(meals.get(0).getStarsTotal());
            log.info("Old votes taken for meal: " + meal.getName());
        }
        return meal;
    }
}
