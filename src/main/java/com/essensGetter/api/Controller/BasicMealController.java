package com.essensGetter.api.Controller;

import com.essensGetter.api.JPA.entities.meals.Generic_Meal;
import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.sun.istack.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface BasicMealController {

    /**
     * Provide the name of the mensa
     */
    Iterable<? extends Mensa> getMensa();

    /**
     * Provides meals from startDate to endDate
     * @param startDate
     * @param enddate
     */
    Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate);

    /**
     * Provide all meals for a specific servingDate
     * @param servingDate
     */
    Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate);

    /**
     * Get all meals for a specific category
     * @param category
     */
    Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category);

    /**
     * Provides all meals for specific category on a specific day
     * @param category
     * @param servingDate
     */
    Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate);

    /**
     * Provides all meals with a rating that is less than
     * @param rating
     */
    Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating);

    /**
     * Provides all meals with a rating that is higher than
     * @param rating
     */
    Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating);

    /**
     * Accepts POST requests and filters out the rating and updates the entries in the database
     * @param receivedMeal
     */
    void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal);

}
