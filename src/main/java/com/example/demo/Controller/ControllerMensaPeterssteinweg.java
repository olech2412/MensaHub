package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.services.meals.Meals_Mensa_PeterssteinwegService;
import com.example.demo.JPA.services.mensen.Mensa_PeterssteinwegService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMensaPeterssteinweg {

    private final Meals_Mensa_PeterssteinwegService meals_mensa_peterssteinwegService;

    private final Mensa_PeterssteinwegService mensa_peterssteinwegService;



    public ControllerMensaPeterssteinweg(Meals_Mensa_PeterssteinwegService meals_mensa_peterssteinwegService, Mensa_PeterssteinwegService mensa_peterssteinwegService) {
        this.meals_mensa_peterssteinwegService = meals_mensa_peterssteinwegService;
        this.mensa_peterssteinwegService = mensa_peterssteinwegService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/mensa_peterssteinweg")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_peterssteinwegService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/mensa_peterssteinweg")
    public void saveMeal(@RequestBody Meals_Schoenauer_Str receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Schoenauer_Str mealFromDB = (Meals_Schoenauer_Str) meals_mensa_peterssteinwegService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_peterssteinwegService.save(mealFromDB, mensa_peterssteinwegService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
