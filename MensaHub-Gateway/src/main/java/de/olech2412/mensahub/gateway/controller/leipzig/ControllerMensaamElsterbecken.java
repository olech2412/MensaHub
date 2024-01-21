package de.olech2412.mensahub.gateway.controller.leipzig;

import de.olech2412.mensahub.gateway.controller.BasicMealController;
import de.olech2412.mensahub.gateway.jpa.services.meals.Meals_Mensa_am_ElsterbeckenService;
import de.olech2412.mensahub.gateway.jpa.services.mensen.Mensa_am_ElsterbeckenService;
import de.olech2412.mensahub.models.Generic_Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Elsterbecken;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/mensa_am_elsterbecken")
@CrossOrigin(origins = "*")
@Timed
public class ControllerMensaamElsterbecken implements BasicMealController {

    private final Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService;

    private final Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService;

    /**
     * Constructor for ControllerMensaamElsterbecken
     *
     * @param meals_mensa_am_elsterbeckenService Service for meals of the mensa
     * @param mensa_am_elsterbeckenService       Service for the mensa
     */
    public ControllerMensaamElsterbecken(Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService, Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService) {
        this.meals_mensa_am_elsterbeckenService = meals_mensa_am_elsterbeckenService;
        this.mensa_am_elsterbeckenService = mensa_am_elsterbeckenService;
    }

    /**
     * Get the name of the mensa
     *
     * @return - the name of the mensa
     */
    @GetMapping("")
    @Cacheable("mensa_am_elsterbecken")
    public Mensa getMensa() {
        log.debug("Mensa info requested");
        return mensa_am_elsterbeckenService.getMensa();
    }

    /**
     * Get all meals of the mensa from startDate until endDate
     *
     * @param startDate - the start date as string (format: yyyy-mm-dd)
     * @param enddate   - the end date as string (format: yyyy-mm-dd)
     * @return - all meals of the mensa from startDate until endDate
     */
    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public List<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_mensa_am_elsterbeckenService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    /**
     * Get all meals of the mensa for a specific date
     *
     * @param servingDate - the date as string (format: yyyy-mm-dd)
     * @return - all meals of the mensa for a specific date
     */
    @GetMapping("/servingDate/{servingDate}")
    public List<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_mensa_am_elsterbeckenService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the mensa for a specific category
     *
     * @param category - the category as string
     * @return - all meals of the mensa for a specific category
     */
    @GetMapping("/category/{category}")
    public List<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_mensa_am_elsterbeckenService.findAllByCategory(category);
    }

    /**
     * Get all meals of the mensa for a specific category and date
     *
     * @param category    - the category as string
     * @param servingDate - the date as string (format: yyyy-mm-dd)
     * @return - all meals of the mensa for a specific category and date
     */
    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public List<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_mensa_am_elsterbeckenService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the mensa where rating is less than a specific value
     *
     * @param rating - the rating as double (specific value)
     * @return - all meals with rating less than the specific value
     */
    @GetMapping("/byRatingLessThen/{rating}")
    public List<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_mensa_am_elsterbeckenService.findAllByRatingLessThanEqual(rating);
    }

    /**
     * Get all meals of the mensa where rating is higher than a specific value
     *
     * @param rating - the rating as double (specific value)
     * @return - all meals with rating higher than the specific value
     */
    @GetMapping("/byRatingHigherThen/{rating}")
    public List<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_mensa_am_elsterbeckenService.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Receive a rating for a meal
     * A complete meal object is needed because it will be identified by name, servingDate and id
     *
     * @param receivedMeal - the meal with the rating
     */
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
