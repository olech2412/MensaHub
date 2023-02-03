package com.example.demo.JPA.services.meals;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Mensa_Academica;
import com.example.demo.JPA.entities.mensen.Mensa;
import com.example.demo.JPA.entities.mensen.Mensa_Academica;
import com.example.demo.JPA.repository.meals.Meals_Mensa_AcademicaRepository;
import com.example.demo.JPA.repository.mensen.Mensa_AcademicaRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_AcademicaService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_AcademicaRepository meals_mensa_academicaRepository;
    @Autowired
    private Mensa_AcademicaRepository mensa_AcademicaRepository;

    /**
     * @return Meals Mensa Academica
     */
    @Override
    public Iterable<Meals_Mensa_Academica> findAll() {
        return meals_mensa_academicaRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Mensa Academica
     */
    @Override
    public List<Meals_Mensa_Academica> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_academicaRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Mensa Academica
     */
    @Override
    public List<Meals_Mensa_Academica> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_academicaRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_Academica meals_mensa_academica = new Meals_Mensa_Academica();
        meals_mensa_academica.setId(meal.getId());
        meals_mensa_academica.setName(meal.getName());
        meals_mensa_academica.setCategory(meal.getCategory());
        meals_mensa_academica.setPrice(meal.getPrice());
        meals_mensa_academica.setServingDate(meal.getServingDate());
        meals_mensa_academica.setDescription(meal.getDescription());
        meals_mensa_academica.setRating(meal.getRating());
        meals_mensa_academica.setVotes(meal.getVotes());
        meals_mensa_academica.setStarsTotal(meal.getStarsTotal());
        meals_mensa_academica.setResponseCode(meal.getResponseCode());

        Mensa_Academica mensa_academica = new Mensa_Academica();
        mensa_academica.setId(mensa.getId());
        mensa_academica.setName(mensa.getName());
        mensa_academica.setApiUrl(mensa.getApiUrl());

        meals_mensa_academica.setMensa_academica(mensa_academica);

        meals_mensa_academicaRepository.save(meals_mensa_academica);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_Academica meals_mensa_academica = new Meals_Mensa_Academica();
        meals_mensa_academica.setId(meal.getId());
        meals_mensa_academica.setName(meal.getName());
        meals_mensa_academica.setCategory(meal.getCategory());
        meals_mensa_academica.setPrice(meal.getPrice());
        meals_mensa_academica.setServingDate(meal.getServingDate());
        meals_mensa_academica.setDescription(meal.getDescription());
        meals_mensa_academica.setRating(meal.getRating());
        meals_mensa_academica.setVotes(meal.getVotes());
        meals_mensa_academica.setStarsTotal(meal.getStarsTotal());
        meals_mensa_academica.setResponseCode(meal.getResponseCode());

        Mensa_Academica mensa_academica = new Mensa_Academica();
        mensa_academica.setId(mensa.getId());
        mensa_academica.setName(mensa.getName());
        mensa_academica.setApiUrl(mensa.getApiUrl());

        meals_mensa_academica.setMensa_academica(mensa_academica);

        meals_mensa_academicaRepository.delete(meals_mensa_academica);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * @param name
     * @param servingDate
     * @param id
     * @return
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_academicaRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }


}

