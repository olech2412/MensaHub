package com.example.demo.JPA.repository.meals;

import com.example.demo.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.example.demo.JPA.entities.meals.Meals_Mensa_Academica;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_AcademicaRepository extends CrudRepository<Meals_Mensa_Academica, Long> {

    List<Meals_Mensa_Academica> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Academica> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_Academica> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}