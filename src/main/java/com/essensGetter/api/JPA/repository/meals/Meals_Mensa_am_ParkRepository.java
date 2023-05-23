package com.essensGetter.api.JPA.repository.meals;

import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Park;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_am_ParkRepository extends CrudRepository<Meals_Mensa_am_Park, Long> {

    List<Meals_Mensa_am_Park> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Park> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Park> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}