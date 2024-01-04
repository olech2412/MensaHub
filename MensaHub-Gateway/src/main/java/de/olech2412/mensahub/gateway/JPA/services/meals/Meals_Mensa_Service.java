package de.olech2412.mensahub.gateway.JPA.services.meals;

import de.olech2412.mensahub.gateway.JPA.services.Abstract_Service;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public abstract class Meals_Mensa_Service extends Abstract_Service { // Meals_Mensa_Service is a subclass of Abstract_Service and provides the following methods for all subclasses

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
