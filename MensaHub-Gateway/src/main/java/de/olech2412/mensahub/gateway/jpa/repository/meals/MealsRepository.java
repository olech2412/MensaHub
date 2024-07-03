package de.olech2412.mensahub.gateway.jpa.repository.meals;

import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealsRepository extends ListCrudRepository<Meal, Long> {

    List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualAndMensaOrderByServingDate(LocalDate startDate, LocalDate endDate, Mensa mensa);

    List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findByNameAndServingDateAndIdAndMensa(String name, LocalDate servingDate, Long id, Mensa mensa);

    List<Meal> findAllByCategoryAndMensa(String category, Mensa mensa);

    List<Meal> findAllByCategoryAndServingDateAndMensa(String category, LocalDate servingDate, Mensa mensa);

    List<Meal> findAllByRatingLessThanEqualAndMensa(Double rating, Mensa mensa);

    List<Meal> findAllByRatingGreaterThanEqualAndMensa(Double rating, Mensa mensa);

}