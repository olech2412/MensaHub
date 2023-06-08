package de.mensahub.gateway.Controller;

import de.mensahub.gateway.JPA.entities.meals.Generic_Meal;
import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Schoenauer_Str;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_Schoenauer_Str;
import de.mensahub.gateway.JPA.services.meals.Meals_Mensa_Schoenauer_StrService;
import de.mensahub.gateway.JPA.services.mensen.Mensa_Schoenauer_StrService;
import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/mensa_schoenauerstr")
@CrossOrigin(origins = "*")
public class ControllerSchoenauerStr {

    private final Meals_Mensa_Schoenauer_StrService meals_mensa_schoenauer_strService;

    private final Mensa_Schoenauer_StrService mensa_schoenauer_strService;


    public ControllerSchoenauerStr(Meals_Mensa_Schoenauer_StrService meals_mensa_schoenauer_strService, Mensa_Schoenauer_StrService mensa_schoenauer_strService) {
        this.meals_mensa_schoenauer_strService = meals_mensa_schoenauer_strService;
        this.mensa_schoenauer_strService = mensa_schoenauer_strService;
    }

    @GetMapping("")
    public Iterable<Mensa_Schoenauer_Str> getMensa() {
        log.debug("Mensa info requested");
        return mensa_schoenauer_strService.findAll();
    }

    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_mensa_schoenauer_strService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_mensa_schoenauer_strService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_mensa_schoenauer_strService.findAllByCategory(category);
    }

    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_mensa_schoenauer_strService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_mensa_schoenauer_strService.findAllByRatingLessThanEqual(rating);
    }

    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_mensa_schoenauer_strService.findAllByRatingGreaterThanEqual(rating);
    }

    @PostMapping("/sendRating")
    public void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal) {
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
