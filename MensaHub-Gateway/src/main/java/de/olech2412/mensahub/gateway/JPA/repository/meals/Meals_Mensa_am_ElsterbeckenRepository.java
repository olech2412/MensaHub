package de.olech2412.mensahub.gateway.JPA.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Elsterbecken;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MealRepositorys are used to create a connection to the database
 */
public interface Meals_Mensa_am_ElsterbeckenRepository extends CrudRepository<Meals_Mensa_am_Elsterbecken, Long> {

    List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Elsterbecken> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Elsterbecken> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Mensa_am_Elsterbecken> findAllByCategory(String category);

    List<Meals_Mensa_am_Elsterbecken> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Mensa_am_Elsterbecken> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Mensa_am_Elsterbecken> findAllByRatingGreaterThanEqual(Double rating);

}