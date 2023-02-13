package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Menseria_am_Botanischen_Garten;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.services.meals.Meals_Mensa_AcademicaService;
import com.example.demo.JPA.services.meals.Meals_Menseria_am_Botanischen_GartenServices;
import com.example.demo.JPA.services.mensen.Mensa_AcademicaService;
import com.example.demo.JPA.services.mensen.Menseria_am_Botanischen_GartenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMenseriaamBotanischenGarten {

    private final Meals_Menseria_am_Botanischen_GartenServices meals_menseria_am_botanischen_gartenServices;

    private final Menseria_am_Botanischen_GartenService menseria_am_botanischen_gartenService;



    public ControllerMenseriaamBotanischenGarten(Meals_Menseria_am_Botanischen_GartenServices meals_menseria_am_botanischen_gartenServices, Menseria_am_Botanischen_GartenService menseria_am_botanischen_gartenService) {
        this.meals_menseria_am_botanischen_gartenServices = meals_menseria_am_botanischen_gartenServices;
        this.menseria_am_botanischen_gartenService = menseria_am_botanischen_gartenService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/menseria_am_botanischen_garten")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_menseria_am_botanischen_gartenServices.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/menseria_am_botanischen_garten")
    public void saveMeal(@RequestBody Meals_Menseria_am_Botanischen_Garten receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Menseria_am_Botanischen_Garten mealFromDB = (Meals_Menseria_am_Botanischen_Garten) meals_menseria_am_botanischen_gartenServices.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_menseria_am_botanischen_gartenServices.save(mealFromDB, menseria_am_botanischen_gartenService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
