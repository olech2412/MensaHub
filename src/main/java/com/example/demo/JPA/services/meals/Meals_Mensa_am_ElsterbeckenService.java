package com.example.demo.JPA.services.meals;


import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;
import com.example.demo.JPA.entities.mensen.Mensa;
import com.example.demo.JPA.entities.mensen.Mensa_am_Elsterbecken;
import com.example.demo.JPA.repository.meals.Meals_Mensa_am_ElsterbeckenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_am_ElsterbeckenService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_am_ElsterbeckenRepository meals_mensa_am_elsterbeckenRepository;

    /**
     * @return Meals Mensa am Elsterbecken
     */
    @Override
    public Iterable<Meals_Mensa_am_Elsterbecken> findAll() {
        return meals_mensa_am_elsterbeckenRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Mensa am Elsterbecken
     */
    @Override
    public List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_am_elsterbeckenRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Mensa am Elsterbecken
     */
    @Override
    public List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_am_elsterbeckenRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Elsterbecken meals_mensa_am_elsterbecken = new Meals_Mensa_am_Elsterbecken();
        meals_mensa_am_elsterbecken.setId(meal.getId());
        meals_mensa_am_elsterbecken.setName(meal.getName());
        meals_mensa_am_elsterbecken.setCategory(meal.getCategory());
        meals_mensa_am_elsterbecken.setPrice(meal.getPrice());
        meals_mensa_am_elsterbecken.setServingDate(meal.getServingDate());
        meals_mensa_am_elsterbecken.setDescription(meal.getDescription());
        meals_mensa_am_elsterbecken.setRating(meal.getRating());
        meals_mensa_am_elsterbecken.setVotes(meal.getVotes());
        meals_mensa_am_elsterbecken.setStarsTotal(meal.getStarsTotal());
        meals_mensa_am_elsterbecken.setResponseCode(meal.getResponseCode());

        Mensa_am_Elsterbecken mensa_am_elsterbecken = new Mensa_am_Elsterbecken();
        mensa_am_elsterbecken.setId(mensa.getId());
        mensa_am_elsterbecken.setName(mensa.getName());
        mensa_am_elsterbecken.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_elsterbecken.setMensa_am_elsterbecken(mensa_am_elsterbecken);

        meals_mensa_am_elsterbeckenRepository.save(meals_mensa_am_elsterbecken);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Elsterbecken meals_mensa_am_elsterbecken = new Meals_Mensa_am_Elsterbecken();
        meals_mensa_am_elsterbecken.setId(meal.getId());
        meals_mensa_am_elsterbecken.setName(meal.getName());
        meals_mensa_am_elsterbecken.setCategory(meal.getCategory());
        meals_mensa_am_elsterbecken.setPrice(meal.getPrice());
        meals_mensa_am_elsterbecken.setServingDate(meal.getServingDate());
        meals_mensa_am_elsterbecken.setDescription(meal.getDescription());
        meals_mensa_am_elsterbecken.setRating(meal.getRating());
        meals_mensa_am_elsterbecken.setVotes(meal.getVotes());
        meals_mensa_am_elsterbecken.setStarsTotal(meal.getStarsTotal());
        meals_mensa_am_elsterbecken.setResponseCode(meal.getResponseCode());

        Mensa_am_Elsterbecken mensa_am_elsterbecken = new Mensa_am_Elsterbecken();
        mensa_am_elsterbecken.setId(mensa.getId());
        mensa_am_elsterbecken.setName(mensa.getName());
        mensa_am_elsterbecken.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_elsterbecken.setMensa_am_elsterbecken(mensa_am_elsterbecken);

        meals_mensa_am_elsterbeckenRepository.delete(meals_mensa_am_elsterbecken);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

}

