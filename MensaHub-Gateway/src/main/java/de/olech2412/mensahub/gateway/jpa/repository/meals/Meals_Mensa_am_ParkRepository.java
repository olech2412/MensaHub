package de.olech2412.mensahub.gateway.jpa.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Park;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MealRepositorys are used to create a connection to the database
 */
public interface Meals_Mensa_am_ParkRepository extends ListCrudRepository<Meals_Mensa_am_Park, Long> {

    List<Meals_Mensa_am_Park> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Park> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Mensa_am_Park> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Park> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Mensa_am_Park> findAllByCategory(String category);

    List<Meals_Mensa_am_Park> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Mensa_am_Park> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Mensa_am_Park> findAllByRatingGreaterThanEqual(Double rating);

}