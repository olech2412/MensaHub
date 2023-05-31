package com.essensGetter.api.Controller;

import com.essensGetter.api.JPA.entities.meals.Generic_Meal;
import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_Peterssteinweg;
import com.essensGetter.api.JPA.entities.mensen.Mensa_Peterssteinweg;
import com.essensGetter.api.JPA.services.meals.Meals_Mensa_PeterssteinwegService;
import com.essensGetter.api.JPA.services.mensen.Mensa_PeterssteinwegService;
import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/mensa_peterssteinweg")
public class ControllerMensaPeterssteinweg {

    private final Meals_Mensa_PeterssteinwegService meals_mensa_peterssteinwegService;

    private final Mensa_PeterssteinwegService mensa_peterssteinwegService;


    public ControllerMensaPeterssteinweg(Meals_Mensa_PeterssteinwegService meals_mensa_peterssteinwegService, Mensa_PeterssteinwegService mensa_peterssteinwegService) {
        this.meals_mensa_peterssteinwegService = meals_mensa_peterssteinwegService;
        this.mensa_peterssteinwegService = mensa_peterssteinwegService;
    }

    @GetMapping("/")
    public Iterable<Mensa_Peterssteinweg> getMensa() {
        log.debug("Mensa info requested");
        return mensa_peterssteinwegService.findAll();
    }

    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_mensa_peterssteinwegService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_mensa_peterssteinwegService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_mensa_peterssteinwegService.findAllByCategory(category);
    }

    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_mensa_peterssteinwegService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_mensa_peterssteinwegService.findAllByRatingLessThanEqual(rating);
    }

    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_mensa_peterssteinwegService.findAllByRatingGreaterThanEqual(rating);
    }

    @PostMapping("/sendRating")
    public void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Mensa_Peterssteinweg mealFromDB = (Meals_Mensa_Peterssteinweg) meals_mensa_peterssteinwegService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
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
