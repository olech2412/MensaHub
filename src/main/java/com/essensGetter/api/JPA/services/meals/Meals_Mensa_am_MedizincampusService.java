package com.essensGetter.api.JPA.services.meals;

import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Medizincampus;
import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.entities.mensen.Mensa_am_Medizincampus;
import com.essensGetter.api.JPA.repository.meals.Meals_Mensa_am_MedizincampusRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_am_MedizincampusService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_am_MedizincampusRepository meals_mensa_am_medizincampusRepository;

    /**
     * @return Meals Mensa am Medizincampus
     */
    @Override
    public Iterable<Meals_Mensa_am_Medizincampus> findAll() {
        return meals_mensa_am_medizincampusRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Medizincampus meals_mensa_am_medizincampus = new Meals_Mensa_am_Medizincampus();
        meals_mensa_am_medizincampus.setId(meal.getId());
        meals_mensa_am_medizincampus.setName(meal.getName());
        meals_mensa_am_medizincampus.setCategory(meal.getCategory());
        meals_mensa_am_medizincampus.setPrice(meal.getPrice());
        meals_mensa_am_medizincampus.setServingDate(meal.getServingDate());
        meals_mensa_am_medizincampus.setDescription(meal.getDescription());
        meals_mensa_am_medizincampus.setRating(meal.getRating());
        meals_mensa_am_medizincampus.setVotes(meal.getVotes());
        meals_mensa_am_medizincampus.setStarsTotal(meal.getStarsTotal());
        meals_mensa_am_medizincampus.setResponseCode(meal.getResponseCode());

        Mensa_am_Medizincampus mensa_am_medizincampus = new Mensa_am_Medizincampus();
        mensa_am_medizincampus.setId(mensa.getId());
        mensa_am_medizincampus.setName(mensa.getName());
        mensa_am_medizincampus.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_medizincampus.setMensa_am_medizincampus(mensa_am_medizincampus);

        meals_mensa_am_medizincampusRepository.save(meals_mensa_am_medizincampus);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Medizincampus meals_mensa_am_medizincampus = new Meals_Mensa_am_Medizincampus();
        meals_mensa_am_medizincampus.setId(meal.getId());
        meals_mensa_am_medizincampus.setName(meal.getName());
        meals_mensa_am_medizincampus.setCategory(meal.getCategory());
        meals_mensa_am_medizincampus.setPrice(meal.getPrice());
        meals_mensa_am_medizincampus.setServingDate(meal.getServingDate());
        meals_mensa_am_medizincampus.setDescription(meal.getDescription());
        meals_mensa_am_medizincampus.setRating(meal.getRating());
        meals_mensa_am_medizincampus.setVotes(meal.getVotes());
        meals_mensa_am_medizincampus.setStarsTotal(meal.getStarsTotal());
        meals_mensa_am_medizincampus.setResponseCode(meal.getResponseCode());

        Mensa_am_Medizincampus mensa_am_medizincampus = new Mensa_am_Medizincampus();
        mensa_am_medizincampus.setId(mensa.getId());
        mensa_am_medizincampus.setName(mensa.getName());
        mensa_am_medizincampus.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_medizincampus.setMensa_am_medizincampus(mensa_am_medizincampus);

        meals_mensa_am_medizincampusRepository.delete(meals_mensa_am_medizincampus);
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
        return meals_mensa_am_medizincampusRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * @param category
     * @return
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_am_medizincampusRepository.findAllByCategory(category);
    }

    /**
     * @param category
     * @param servingDate
     * @return
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * @param rating
     * @return
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_am_medizincampusRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * @param rating
     * @return
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_am_medizincampusRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_am_medizincampusRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }
}

