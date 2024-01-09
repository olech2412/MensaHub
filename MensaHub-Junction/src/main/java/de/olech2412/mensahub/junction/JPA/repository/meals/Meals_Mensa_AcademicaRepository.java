package de.olech2412.mensahub.junction.JPA.repository.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Academica;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_AcademicaRepository extends CrudRepository<Meals_Mensa_Academica, Long> {

    List<Meals_Mensa_Academica> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Academica> findAllMealsByServingDate(LocalDate servingDate);

}