package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Meal;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

@Import({Meals_Cafeteria_Dittrichring.class, Meal.class})
public interface Meals_Cafeteria_DittrichringRepository extends CrudRepository<Meals_Cafeteria_Dittrichring, Long> {

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDate(LocalDate servingDate);

    void deleteAllByServingDate(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findMeals_Cafeteria_DittrichringByNameAndServingDateBeforeOrderByServingDateDesc(String name, LocalDate servingDate);

}