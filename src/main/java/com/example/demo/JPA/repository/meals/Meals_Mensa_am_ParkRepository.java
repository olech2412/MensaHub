package com.example.demo.JPA.repository.meals;

import com.example.demo.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.example.demo.JPA.entities.meals.Meals_Mensa_am_Park;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_am_ParkRepository extends CrudRepository<Meals_Mensa_am_Park, Long> {

    List<Meals_Mensa_am_Park> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Park> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Park> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}