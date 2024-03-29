package de.olech2412.mensahub.gateway.jpa.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Tierklinik;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MealRepositorys are used to create a connection to the database
 */
public interface Meals_Mensa_TierklinikRepository extends ListCrudRepository<Meals_Mensa_Tierklinik, Long> {

    List<Meals_Mensa_Tierklinik> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Tierklinik> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Mensa_Tierklinik> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_Tierklinik> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Mensa_Tierklinik> findAllByCategory(String category);

    List<Meals_Mensa_Tierklinik> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Mensa_Tierklinik> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Mensa_Tierklinik> findAllByRatingGreaterThanEqual(Double rating);

}