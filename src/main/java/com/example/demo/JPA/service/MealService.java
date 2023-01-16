package com.example.demo.JPA.service;

import com.example.demo.JPA.Meal;
import com.example.demo.JPA.repository.MealRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MealService{

    @Autowired
    MealRepository mealRepository;

    /**
     * Saves a meal to the database
     * @param meal
     */
    public void saveMeal(Meal meal){
        mealRepository.save(meal);
    }

    /**
     * Checks if a meal already exists in the database
     * @param name
     * @return
     */
    public Meal findByName(String name){
        return mealRepository.findByName(name);
    }

    /**
     * Checks if a meal already exists in the database
     * @param meal
     */
    public void deleteMeal(Meal meal){
        mealRepository.delete(meal);
    }

    /**
     * Deletes all meals from the database
     */
    public void deleteAllMeals(){
        mealRepository.deleteAll();
    }

    /**
     * finds all meals in the database
     * @return
     */
    public Iterable<Meal> findAllMeals(){
        return mealRepository.findAll();
    }

    /**
     * Updates a meal in the database
     * @param meal
     * @return
     */
    public void updateMeal(Meal meal){
        mealRepository.save(meal);
    }

    /**
     * Save all meals from the list to the database
     * @return
     */
    public void saveAllMeals(Iterable<Meal> meals){
        mealRepository.saveAll(meals);
        log.info("Saved all meals to the database");
    }

}
