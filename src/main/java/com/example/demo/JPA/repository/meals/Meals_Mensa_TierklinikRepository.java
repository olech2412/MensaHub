package com.example.demo.JPA.repository.meals;

import com.example.demo.JPA.entities.meals.Meals_Mensa_Tierklinik;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_TierklinikRepository extends CrudRepository<Meals_Mensa_Tierklinik, Long> {

    List<Meals_Mensa_Tierklinik> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_Tierklinik> findAllMealsByServingDate(LocalDate servingDate);

}