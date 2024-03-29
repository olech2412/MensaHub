package de.olech2412.mensahub.gateway.jpa.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Schoenauer_Str;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MealRepositorys are used to create a connection to the database
 */
public interface Meals_Schoenauer_StrRepository extends ListCrudRepository<Meals_Schoenauer_Str, Long> {

    List<Meals_Schoenauer_Str> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Schoenauer_Str> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Schoenauer_Str> findAllByCategory(String category);

    List<Meals_Schoenauer_Str> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Schoenauer_Str> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Schoenauer_Str> findAllByRatingGreaterThanEqual(Double rating);

}