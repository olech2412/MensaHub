package de.mensahub.gateway.JPA.repository.meals;

import de.mensahub.gateway.JPA.entities.meals.Meals_Schoenauer_Str;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Schoenauer_StrRepository extends CrudRepository<Meals_Schoenauer_Str, Long> {

    List<Meals_Schoenauer_Str> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Schoenauer_Str> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Schoenauer_Str> findAllByCategory(String category);

    List<Meals_Schoenauer_Str> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Schoenauer_Str> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Schoenauer_Str> findAllByRatingGreaterThanEqual(Double rating);

}