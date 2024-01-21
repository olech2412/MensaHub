package de.olech2412.mensahub.gateway.controller.leipzig;

import de.olech2412.mensahub.gateway.controller.BasicMealController;
import de.olech2412.mensahub.gateway.jpa.services.meals.Meals_Cafeteria_DittrichringService;
import de.olech2412.mensahub.gateway.jpa.services.mensen.Cafeteria_DittrichringService;
import de.olech2412.mensahub.models.Generic_Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
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
@RequestMapping("/cafeteria_dittrichring")
@CrossOrigin(origins = "*")
@Timed
public class ControllerCafeteriaDittrichring implements BasicMealController {

    private final Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService;

    private final Cafeteria_DittrichringService cafeteria_dittrichringService;

    /**
     * Constructor for ControllerCafeteriaDittrichring
     *
     * @param meals_cafeteria_dittrichringService Service for meals of the cafeteria
     * @param cafeteria_dittrichringService       Service for the cafeteria
     */
    public ControllerCafeteriaDittrichring(Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService, Cafeteria_DittrichringService cafeteria_dittrichringService) {
        this.meals_cafeteria_dittrichringService = meals_cafeteria_dittrichringService;
        this.cafeteria_dittrichringService = cafeteria_dittrichringService;
    }

    /**
     * Get the name of the cafeteria
     *
     * @return - The name of the cafeteria
     */
    @GetMapping("")
    @Cacheable("mensa_dittrichring")
    public Mensa getMensa() {
        log.debug("Mensa info requested");
        return cafeteria_dittrichringService.getMensa();
    }

    /**
     * Get all meals of the cafeteria from startDate until endDate
     *
     * @param startDate - The start date as a string (yyyy-MM-dd)
     * @param enddate   - The end date as a string (yyyy-MM-dd)
     * @return - All meals between the start and end date
     */
    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public List<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_cafeteria_dittrichringService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    /**
     * Get all meals of the cafeteria for a specific date
     *
     * @param servingDate - The date as a string (yyyy-MM-dd)
     * @return - All meals with the specific date
     */
    @GetMapping("/servingDate/{servingDate}")
    public List<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_cafeteria_dittrichringService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the cafeteria for a specific category
     *
     * @param category - The category as a string
     * @return - All meals with the specific category
     */
    @GetMapping("/category/{category}")
    public List<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_cafeteria_dittrichringService.findAllByCategory(category);
    }

    /**
     * Get all meals of the cafeteria for a specific category and date
     *
     * @param category    - The category as a string
     * @param servingDate - The date as a string (yyyy-MM-dd)
     * @return - All meals with the specific category and date
     */
    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public List<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_cafeteria_dittrichringService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the cafeteria where rating is lower than a specific value
     *
     * @param rating - The rating as a double value (specific value)
     * @return - All meals with a rating lower than the specific value
     */
    @GetMapping("/byRatingLessThen/{rating}")
    public List<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_cafeteria_dittrichringService.findAllByRatingLessThanEqual(rating);
    }

    /**
     * Get all meals of the cafeteria where rating is higher than a specific value
     *
     * @param rating - The rating as a double value (specific value)
     * @return - All meals with a rating higher than the specific value
     */
    @GetMapping("/byRatingHigherThen/{rating}")
    public List<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_cafeteria_dittrichringService.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Receive a rating for a meal
     * A complete meal object is needed because it will be identified by name, servingDate and id
     *
     * @param receivedMeal - The meal object with the rating
     */
    @Override
    @PostMapping("/sendRating")
    public void saveRatingForMeal(Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Cafeteria_Dittrichring mealFromDB = (Meals_Cafeteria_Dittrichring) meals_cafeteria_dittrichringService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0); // Identifies the meal by name, servingDate and id
        if (mealFromDB != null) { // If meal was found in DB
            mealFromDB.setVotes(mealFromDB.getVotes() + 1); // Increase the number of votes
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating())); // Increase the total number of stars
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes()); // Calculate the new rating
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", "."))); // Set the new rating
            meals_cafeteria_dittrichringService.save(mealFromDB, cafeteria_dittrichringService.getMensa()); // Save the meal
        } else {
            log.error("Meal was not found in DB");
        }
    }

}
