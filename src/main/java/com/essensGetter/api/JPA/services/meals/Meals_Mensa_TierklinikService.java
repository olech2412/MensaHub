package com.essensGetter.api.JPA.services.meals;

import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_Tierklinik;
import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.entities.mensen.Mensa_Tierklinik;
import com.essensGetter.api.JPA.repository.meals.Meals_Mensa_TierklinikRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_TierklinikService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_TierklinikRepository meals_mensa_tierklinikRepository;

    /**
     * @return Meals Mensa Tierklinik
     */
    @Override
    public Iterable<Meals_Mensa_Tierklinik> findAll() {
        return meals_mensa_tierklinikRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Mensa Tierklinik
     */
    @Override
    public List<Meals_Mensa_Tierklinik> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_tierklinikRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Mensa Tierklinik
     */
    @Override
    public List<Meals_Mensa_Tierklinik> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_tierklinikRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_Tierklinik meals_mensa_tierklinik = new Meals_Mensa_Tierklinik();
        meals_mensa_tierklinik.setId(meal.getId());
        meals_mensa_tierklinik.setName(meal.getName());
        meals_mensa_tierklinik.setCategory(meal.getCategory());
        meals_mensa_tierklinik.setPrice(meal.getPrice());
        meals_mensa_tierklinik.setServingDate(meal.getServingDate());
        meals_mensa_tierklinik.setDescription(meal.getDescription());
        meals_mensa_tierklinik.setRating(meal.getRating());
        meals_mensa_tierklinik.setVotes(meal.getVotes());
        meals_mensa_tierklinik.setStarsTotal(meal.getStarsTotal());
        meals_mensa_tierklinik.setResponseCode(meal.getResponseCode());

        Mensa_Tierklinik mensa_tierklinik = new Mensa_Tierklinik();
        mensa_tierklinik.setId(mensa.getId());
        mensa_tierklinik.setName(mensa.getName());
        mensa_tierklinik.setApiUrl(mensa.getApiUrl());

        meals_mensa_tierklinik.setMensa_tierklinik(mensa_tierklinik);

        meals_mensa_tierklinikRepository.save(meals_mensa_tierklinik);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_Tierklinik meals_mensa_tierklinik = new Meals_Mensa_Tierklinik();
        meals_mensa_tierklinik.setId(meal.getId());
        meals_mensa_tierklinik.setName(meal.getName());
        meals_mensa_tierklinik.setCategory(meal.getCategory());
        meals_mensa_tierklinik.setPrice(meal.getPrice());
        meals_mensa_tierklinik.setServingDate(meal.getServingDate());
        meals_mensa_tierklinik.setDescription(meal.getDescription());
        meals_mensa_tierklinik.setRating(meal.getRating());
        meals_mensa_tierklinik.setVotes(meal.getVotes());
        meals_mensa_tierklinik.setStarsTotal(meal.getStarsTotal());
        meals_mensa_tierklinik.setResponseCode(meal.getResponseCode());

        Mensa_Tierklinik mensa_tierklinik = new Mensa_Tierklinik();
        mensa_tierklinik.setId(mensa.getId());
        mensa_tierklinik.setName(mensa.getName());
        mensa_tierklinik.setApiUrl(mensa.getApiUrl());

        meals_mensa_tierklinik.setMensa_tierklinik(mensa_tierklinik);

        meals_mensa_tierklinikRepository.delete(meals_mensa_tierklinik);
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
        return meals_mensa_tierklinikRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * @param category
     * @return
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_tierklinikRepository.findAllByCategory(category);
    }

    /**
     * @param category
     * @param servingDate
     * @return
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_tierklinikRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * @param rating
     * @return
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_tierklinikRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * @param rating
     * @return
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_tierklinikRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_tierklinikRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }
}

