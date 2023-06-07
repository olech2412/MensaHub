package de.mensahub.gateway.JPA.services.meals;

import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_Peterssteinweg;
import de.mensahub.gateway.JPA.entities.mensen.Mensa;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_Peterssteinweg;
import de.mensahub.gateway.JPA.repository.meals.Meals_Mensa_PeterssteinwegRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_PeterssteinwegService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_PeterssteinwegRepository meals_mensa_peterssteinwegRepository;

    /**
     * @return Meals Mensa Peterssteinweg
     */
    @Override
    public Iterable<Meals_Mensa_Peterssteinweg> findAll() {
        return meals_mensa_peterssteinwegRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Mensa Peterssteinweg
     */
    @Override
    public List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_peterssteinwegRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Mensa Peterssteinweg
     */
    @Override
    public List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_peterssteinwegRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_Peterssteinweg meals_mensa_peterssteinweg = new Meals_Mensa_Peterssteinweg();
        meals_mensa_peterssteinweg.setId(meal.getId());
        meals_mensa_peterssteinweg.setName(meal.getName());
        meals_mensa_peterssteinweg.setCategory(meal.getCategory());
        meals_mensa_peterssteinweg.setPrice(meal.getPrice());
        meals_mensa_peterssteinweg.setServingDate(meal.getServingDate());
        meals_mensa_peterssteinweg.setDescription(meal.getDescription());
        meals_mensa_peterssteinweg.setRating(meal.getRating());
        meals_mensa_peterssteinweg.setVotes(meal.getVotes());
        meals_mensa_peterssteinweg.setStarsTotal(meal.getStarsTotal());
        meals_mensa_peterssteinweg.setResponseCode(meal.getResponseCode());

        Mensa_Peterssteinweg mensa_peterssteinweg = new Mensa_Peterssteinweg();
        mensa_peterssteinweg.setId(mensa.getId());
        mensa_peterssteinweg.setName(mensa.getName());
        mensa_peterssteinweg.setApiUrl(mensa.getApiUrl());

        meals_mensa_peterssteinweg.setMensa_peterssteinweg(mensa_peterssteinweg);

        meals_mensa_peterssteinwegRepository.save(meals_mensa_peterssteinweg);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_Peterssteinweg meals_mensa_peterssteinweg = new Meals_Mensa_Peterssteinweg();
        meals_mensa_peterssteinweg.setId(meal.getId());
        meals_mensa_peterssteinweg.setName(meal.getName());
        meals_mensa_peterssteinweg.setCategory(meal.getCategory());
        meals_mensa_peterssteinweg.setPrice(meal.getPrice());
        meals_mensa_peterssteinweg.setServingDate(meal.getServingDate());
        meals_mensa_peterssteinweg.setDescription(meal.getDescription());
        meals_mensa_peterssteinweg.setRating(meal.getRating());
        meals_mensa_peterssteinweg.setVotes(meal.getVotes());
        meals_mensa_peterssteinweg.setStarsTotal(meal.getStarsTotal());
        meals_mensa_peterssteinweg.setResponseCode(meal.getResponseCode());

        Mensa_Peterssteinweg mensa_peterssteinweg = new Mensa_Peterssteinweg();
        mensa_peterssteinweg.setId(mensa.getId());
        mensa_peterssteinweg.setName(mensa.getName());
        mensa_peterssteinweg.setApiUrl(mensa.getApiUrl());

        meals_mensa_peterssteinweg.setMensa_peterssteinweg(mensa_peterssteinweg);

        meals_mensa_peterssteinwegRepository.delete(meals_mensa_peterssteinweg);
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
        return meals_mensa_peterssteinwegRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * @param category
     * @return
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_peterssteinwegRepository.findAllByCategory(category);
    }

    /**
     * @param category
     * @param servingDate
     * @return
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_peterssteinwegRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * @param rating
     * @return
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_peterssteinwegRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * @param rating
     * @return
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_peterssteinwegRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_peterssteinwegRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }
}

