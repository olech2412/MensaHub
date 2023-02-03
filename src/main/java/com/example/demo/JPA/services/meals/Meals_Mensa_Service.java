package com.example.demo.JPA.services.meals;


import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.mensen.Mensa;
import com.example.demo.JPA.services.Abstract_Service;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public abstract class Meals_Mensa_Service extends Abstract_Service {

    @Override
    public abstract Iterable<? extends Meal> findAll();

    public abstract List<? extends Meal> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    public abstract List<? extends Meal> findAllMealsByServingDate(LocalDate servingDate);

    public abstract void save(Meal meal, Mensa mensa);

    public abstract void delete(Meal meal, Mensa mensa);

}
