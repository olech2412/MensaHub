package de.olech2412.mensahub.gateway.JPA.repository.meals;

import de.mensahub.gateway.JPA.entities.meals.Meals_Menseria_am_Botanischen_Garten;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * MealRepositorys are used to create a connection to the database
 */
public interface Meals_Menseria_am_Botanischen_GartenRepository extends CrudRepository<Meals_Menseria_am_Botanischen_Garten, Long> {

    List<Meals_Menseria_am_Botanischen_Garten> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Menseria_am_Botanischen_Garten> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Menseria_am_Botanischen_Garten> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Menseria_am_Botanischen_Garten> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Menseria_am_Botanischen_Garten> findAllByCategory(String category);

    List<Meals_Menseria_am_Botanischen_Garten> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Menseria_am_Botanischen_Garten> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Menseria_am_Botanischen_Garten> findAllByRatingGreaterThanEqual(Double rating);

}