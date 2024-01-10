package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Peterssteinweg;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_PeterssteinwegRepository extends CrudRepository<Meals_Mensa_Peterssteinweg, Long> {

    List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDate(LocalDate servingDate);

    void deleteAllByServingDate(LocalDate servingDate);

    List<Meals_Mensa_Peterssteinweg> findMeals_Mensa_PeterssteinwegByNameAndServingDateBeforeOrderByServingDateDesc(String name, LocalDate servingDate);
}