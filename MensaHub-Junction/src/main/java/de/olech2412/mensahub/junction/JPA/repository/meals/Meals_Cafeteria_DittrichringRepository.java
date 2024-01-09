package de.olech2412.mensahub.junction.JPA.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Cafeteria_DittrichringRepository extends CrudRepository<Meals_Cafeteria_Dittrichring, Long> {

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDate(LocalDate servingDate);

}