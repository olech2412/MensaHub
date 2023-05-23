package com.essensGetter.api.JPA.repository.meals;

import com.essensGetter.api.JPA.entities.meals.Meals_Schoenauer_Str;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Schoenauer_StrRepository extends CrudRepository<Meals_Schoenauer_Str, Long> {

    List<Meals_Schoenauer_Str> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Schoenauer_Str> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}