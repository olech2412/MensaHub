package de.mensahub.gateway.Controller;

import de.mensahub.gateway.JPA.entities.meals.Generic_Meal;
import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Elsterbecken;
import de.mensahub.gateway.JPA.services.meals.Meals_Mensa_am_ElsterbeckenService;
import de.mensahub.gateway.JPA.services.mensen.Mensa_am_ElsterbeckenService;
import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/mensa_am_elsterbecken")
@CrossOrigin(origins = "*")
public class ControllerMensaamElsterbecken implements BasicMealController {

    private final Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService;

    private final Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService;


    public ControllerMensaamElsterbecken(Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService, Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService) {
        this.meals_mensa_am_elsterbeckenService = meals_mensa_am_elsterbeckenService;
        this.mensa_am_elsterbeckenService = mensa_am_elsterbeckenService;
    }

    @GetMapping("")
    public Iterable<Mensa_am_Elsterbecken> getMensa() {
        log.debug("Mensa info requested");
        return mensa_am_elsterbeckenService.findAll();
    }

    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_mensa_am_elsterbeckenService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_mensa_am_elsterbeckenService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_mensa_am_elsterbeckenService.findAllByCategory(category);
    }

    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_mensa_am_elsterbeckenService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_mensa_am_elsterbeckenService.findAllByRatingLessThanEqual(rating);
    }

    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_mensa_am_elsterbeckenService.findAllByRatingGreaterThanEqual(rating);
    }

    @PostMapping("/sendRating")
    public void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Mensa_am_Elsterbecken mealFromDB = (Meals_Mensa_am_Elsterbecken) meals_mensa_am_elsterbeckenService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_am_elsterbeckenService.save(mealFromDB, mensa_am_elsterbeckenService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
