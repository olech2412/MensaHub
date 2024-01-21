package de.olech2412.mensahub.junction.JPA.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Schoenauer_Str;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Schoenauer_StrRepository extends ListCrudRepository<Meals_Schoenauer_Str, Long> {

    List<Meals_Schoenauer_Str> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findAllMealsByServingDate(LocalDate servingDate);

}