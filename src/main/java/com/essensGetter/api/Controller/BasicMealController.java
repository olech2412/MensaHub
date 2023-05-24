package com.essensGetter.api.Controller;

import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.sun.istack.NotNull;
import org.springframework.web.bind.annotation.PathVariable;

public interface BasicMealController {

    /**
     * Provide the name of the mensa
     */
    Iterable<? extends Mensa> getMensa();

    /**
     * Provides the meals that will be available for the next x days
     * @param days
     */
    Iterable<? extends Meal> getMealsNextDays(@PathVariable Integer days);

    Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate);

    Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category);

    Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate);

    Iterable<? extends Meal> getMealByRatingLessThen(@PathVariable("rating") @NotNull Double rating);

    Iterable<? extends Meal> getMealByRatingHigherThen(@PathVariable("rating") @NotNull Double rating);

}
