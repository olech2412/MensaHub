package de.olech2412.mensahub.junction.JPA.repository.meals;

import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends ListCrudRepository<Meal, Long> {

    List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa);

}