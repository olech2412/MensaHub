package com.example.demo.JPA.repository.meals;

import com.example.demo.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Cafeteria_DittrichringRepository extends CrudRepository<Meals_Cafeteria_Dittrichring, Long> {

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Cafeteria_Dittrichring> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

}