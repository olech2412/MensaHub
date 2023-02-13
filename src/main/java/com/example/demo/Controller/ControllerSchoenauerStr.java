package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.services.meals.Meals_Mensa_Schoenauer_StrService;
import com.example.demo.JPA.services.mensen.Mensa_Schoenauer_StrService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerSchoenauerStr {

    private final Meals_Mensa_Schoenauer_StrService meals_mensa_schoenauer_strService;

    private final Mensa_Schoenauer_StrService mensa_schoenauer_strService;



    public ControllerSchoenauerStr(Meals_Mensa_Schoenauer_StrService meals_mensa_schoenauer_strService, Mensa_Schoenauer_StrService mensa_schoenauer_strService) {
        this.meals_mensa_schoenauer_strService = meals_mensa_schoenauer_strService;
        this.mensa_schoenauer_strService = mensa_schoenauer_strService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/mensa_schoenauer_str")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_schoenauer_strService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/mensa_schoenauer_str")
    public void saveMeal(@RequestBody Meals_Schoenauer_Str receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Schoenauer_Str mealFromDB = (Meals_Schoenauer_Str) meals_mensa_schoenauer_strService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_schoenauer_strService.save(mealFromDB, mensa_schoenauer_strService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
