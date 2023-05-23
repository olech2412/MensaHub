package com.essensGetter.api.JPA.repository.meals;

import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_am_ElsterbeckenRepository extends CrudRepository<Meals_Mensa_am_Elsterbecken, Long> {

    List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Elsterbecken> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}