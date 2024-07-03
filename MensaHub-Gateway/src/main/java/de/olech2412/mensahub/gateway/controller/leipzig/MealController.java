package de.olech2412.mensahub.gateway.controller.leipzig;

import de.olech2412.mensahub.gateway.jpa.services.meals.MealsService;
import de.olech2412.mensahub.gateway.jpa.services.mensen.MensaService;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/meal")
@CrossOrigin(origins = "*")
@Timed
public class MealController {

    private final MealsService mealsService;

    private final MensaService mensaService;

    /**
     * Constructor for MealController
     *
     * @param mealsService Service for meals of the cafeteria
     * @param mensaService Service for the cafeteria
     */
    public MealController(MealsService mealsService, MensaService mensaService) {
        this.mealsService = mealsService;
        this.mensaService = mensaService;
    }

    /**
     * Get all meals of the mensa from startDate until endDate
     *
     * @param startDate - The start date as a string (yyyy-MM-dd)
     * @param enddate   - The end date as a string (yyyy-MM-dd)
     * @param mensaId   - The ID of the mensa
     * @return - All meals between the start and end date
     */
    @GetMapping("/getMeals/from/{startDate}/to/{enddate}/fromMensa/{mensaId}")
    public List<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate, @PathVariable long mensaId) {
        log.info("Meals were requested from {} until {}", startDate, enddate);
        return mealsService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualAndMensaOrderByServingDate(LocalDate.parse(startDate), LocalDate.parse(enddate), mensaService.getMensa(mensaId));
    }

    /**
     * Get all meals of the cafeteria for a specific date
     *
     * @param servingDate - The date as a string (yyyy-MM-dd)
     * @return - All meals with the specific date
     */
    @GetMapping("/servingDate/{servingDate}/fromMensa/{mensaId}")
    public List<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate, @PathVariable long mensaId) {
        log.info("Meals were requested with servingDate: {}", servingDate);
        return mealsService.findAllMealsByServingDateAndMensa(LocalDate.parse(servingDate), mensaService.getMensa(mensaId));
    }

    /**
     * Get all meals of the cafeteria for a specific category
     *
     * @param category - The category as a string
     * @return - All meals with the specific category
     */
    @GetMapping("/category/{category}/fromMensa/{mensaId}")
    public List<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category, @PathVariable long mensaId) {
        log.info("Meals were requested with category: {}", category);
        return mealsService.findAllByCategoryAndMensa(category, mensaService.getMensa(mensaId));
    }

    /**
     * Get all meals of the cafeteria for a specific category and date
     *
     * @param category    - The category as a string
     * @param servingDate - The date as a string (yyyy-MM-dd)
     * @return - All meals with the specific category and date
     */
    @GetMapping("/category/{category}/servingDate/{servingDate}/fromMensa/{mensaId}")
    public List<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate, @PathVariable long mensaId) {
        log.info("Meals were requested with category: " + category + " on " + servingDate);
        return mealsService.findAllByCategoryAndServingDateAndMensa(category, LocalDate.parse(servingDate), mensaService.getMensa(mensaId));
    }

    /**
     * Get all meals of the cafeteria where rating is lower than a specific value
     *
     * @param rating - The rating as a double value (specific value)
     * @return - All meals with a rating lower than the specific value
     */
    @GetMapping("/byRatingLessThen/{rating}/fromMensa/{mensaId}")
    public List<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating, @PathVariable long mensaId) {
        log.info("Meals were requested with rating less then: {}", rating);
        return mealsService.findAllByRatingLessThanEqualAndMensa(rating, mensaService.getMensa(mensaId));
    }

    /**
     * Get all meals of the cafeteria where rating is higher than a specific value
     *
     * @param rating - The rating as a double value (specific value)
     * @return - All meals with a rating higher than the specific value
     */
    @GetMapping("/byRatingHigherThen/{rating}/fromMensa/{mensaId}")
    public List<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating, @PathVariable long mensaId) {
        log.info("Meals were requested with rating higher then: {}", rating);
        return mealsService.findAllByRatingGreaterThanEqualAndMensa(rating, mensaService.getMensa(mensaId));
    }

    /**
     * Receive a rating for a meal
     * A complete meal object is needed because it will be identified by name, servingDate and id
     *
     * @param receivedMeal - The meal object with the rating
     */
    @PostMapping("/sendRating/fromMensa/{mensaId}")
    public void saveRatingForMeal(Meal receivedMeal, @PathVariable long mensaId) {
        log.info("Meal received: {}", receivedMeal);
        Mensa mensa = mensaService.getMensa(mensaId);

        if (mensa == null) {
            log.error("Mensa was not found in DB");
            return;
        }

        Meal mealFromDB = mealsService.findByNameAndServingDateAndIdAndMensa(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId(), mensa).get(0); // Identifies the meal by name, servingDate and id
        if (mealFromDB != null) { // If meal was found in DB
            mealFromDB.setVotes(mealFromDB.getVotes() + 1); // Increase the number of votes
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating())); // Increase the total number of stars
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes()); // Calculate the new rating
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", "."))); // Set the new rating
            mealsService.save(mealFromDB, mensa); // Save the meal
        } else {
            log.error("Meal was not found in DB");
        }
    }

}
