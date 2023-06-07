package de.mensahub.gateway.Controller;

import de.mensahub.gateway.JPA.entities.meals.Generic_Meal;
import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import de.mensahub.gateway.JPA.entities.mensen.Cafeteria_Dittrichring;
import de.mensahub.gateway.JPA.services.meals.Meals_Cafeteria_DittrichringService;
import de.mensahub.gateway.JPA.services.mensen.Cafeteria_DittrichringService;
import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/cafeteria_dittrichring")
@CrossOrigin(origins = "https://mensi-mates.whosfritz.de")
public class ControllerCafeteriaDittrichring implements BasicMealController {

    private final Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService;

    private final Cafeteria_DittrichringService cafeteria_dittrichringService;


    public ControllerCafeteriaDittrichring(Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService, Cafeteria_DittrichringService cafeteria_dittrichringService) {
        this.meals_cafeteria_dittrichringService = meals_cafeteria_dittrichringService;
        this.cafeteria_dittrichringService = cafeteria_dittrichringService;
    }

    @GetMapping("")
    public Iterable<Cafeteria_Dittrichring> getMensa() {
        log.debug("Mensa info requested");
        return cafeteria_dittrichringService.findAll();
    }

    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_cafeteria_dittrichringService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_cafeteria_dittrichringService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_cafeteria_dittrichringService.findAllByCategory(category);
    }

    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_cafeteria_dittrichringService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_cafeteria_dittrichringService.findAllByRatingLessThanEqual(rating);
    }

    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_cafeteria_dittrichringService.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * @param receivedMeal
     */
    @Override
    @PostMapping("/sendRating")
    public void saveRatingForMeal(Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Cafeteria_Dittrichring mealFromDB = (Meals_Cafeteria_Dittrichring) meals_cafeteria_dittrichringService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_cafeteria_dittrichringService.save(mealFromDB, cafeteria_dittrichringService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }

}
