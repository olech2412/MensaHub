package com.example.demo.JPA.repository.meals;

import com.example.demo.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.example.demo.JPA.entities.meals.Meals_Mensa_Peterssteinweg;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_PeterssteinwegRepository extends CrudRepository<Meals_Mensa_Peterssteinweg, Long> {

    List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_Peterssteinweg> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}