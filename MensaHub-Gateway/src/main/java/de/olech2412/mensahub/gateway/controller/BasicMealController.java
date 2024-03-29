package de.olech2412.mensahub.gateway.controller;

import de.olech2412.mensahub.models.Generic_Meal;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Interface for the basic meal controller
 * This interface is used to provide the basic methods for the meal controllers
 */
public interface BasicMealController {

    /**
     * Provide the name of the mensa
     */
    Mensa getMensa();

    /**
     * Provides meals from startDate to endDate
     *
     * @param startDate
     * @param enddate
     */
    List<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate);

    /**
     * Provide all meals for a specific servingDate
     *
     * @param servingDate
     */
    List<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate);

    /**
     * Get all meals for a specific category
     *
     * @param category
     */
    List<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category);

    /**
     * Provides all meals for specific category on a specific day
     *
     * @param category
     * @param servingDate
     */
    List<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate);

    /**
     * Provides all meals with a rating that is less than
     *
     * @param rating
     */
    List<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating);

    /**
     * Provides all meals with a rating that is higher than
     *
     * @param rating
     */
    List<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating);

    /**
     * Accepts POST requests and filters out the rating and updates the entries in the database
     *
     * @param receivedMeal
     */
    void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal);

}
