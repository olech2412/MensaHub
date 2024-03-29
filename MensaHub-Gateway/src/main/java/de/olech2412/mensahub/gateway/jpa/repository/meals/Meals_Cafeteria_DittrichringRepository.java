package de.olech2412.mensahub.gateway.jpa.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MealRepositorys are used to create a connection to the database
 */
public interface Meals_Cafeteria_DittrichringRepository extends ListCrudRepository<Meals_Cafeteria_Dittrichring, Long> {

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Cafeteria_Dittrichring> findAllByCategory(String category);

    List<Meals_Cafeteria_Dittrichring> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Cafeteria_Dittrichring> findAllByRatingGreaterThanEqual(Double rating);

}