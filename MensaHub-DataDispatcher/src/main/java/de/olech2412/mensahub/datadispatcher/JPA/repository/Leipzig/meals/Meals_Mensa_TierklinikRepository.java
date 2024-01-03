package de.olech2412.mensahub.datadispatcher.JPA.repository.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Tierklinik;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_TierklinikRepository extends CrudRepository<Meals_Mensa_Tierklinik, Long> {

    List<Meals_Mensa_Tierklinik> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Tierklinik> findAllMealsByServingDate(LocalDate servingDate);

    void deleteAllByServingDate(LocalDate servingDate);

    List<Meals_Mensa_Tierklinik> findMeals_Mensa_TierklinikByNameAndServingDateBeforeOrderByServingDateDesc(String name, LocalDate servingDate);

}