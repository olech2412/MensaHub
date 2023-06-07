package de.mensahub.gateway.JPA.repository.meals;

import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_Academica;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_AcademicaRepository extends CrudRepository<Meals_Mensa_Academica, Long> {

    List<Meals_Mensa_Academica> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Academica> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Mensa_Academica> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_Academica> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Mensa_Academica> findAllByCategory(String category);

    List<Meals_Mensa_Academica> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Mensa_Academica> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Mensa_Academica> findAllByRatingGreaterThanEqual(Double rating);

}