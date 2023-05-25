package com.essensGetter.api.JPA.services.meals;


import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.services.Abstract_Service;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public abstract class Meals_Mensa_Service extends Abstract_Service {

    @Override
    public abstract Iterable<? extends Meal> findAll();

    public abstract List<? extends Meal> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    public abstract List<? extends Meal> findAllMealsByServingDate(LocalDate servingDate);

    public abstract void save(Meal meal, Mensa mensa);

    public abstract void delete(Meal meal, Mensa mensa);

    public abstract List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    public abstract List<? extends Meal> findAllByCategory(String category);

    public abstract List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    public abstract List<? extends Meal> findAllByRatingLessThanEqual(Double rating);

    public abstract List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating);

    public abstract List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate);
}
