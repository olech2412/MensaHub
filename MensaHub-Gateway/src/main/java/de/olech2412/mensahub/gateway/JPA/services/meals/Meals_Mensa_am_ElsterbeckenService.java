package de.olech2412.mensahub.gateway.JPA.services.meals;

import de.olech2412.mensahub.gateway.JPA.repository.meals.Meals_Mensa_am_ElsterbeckenRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Elsterbecken;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Elsterbecken;
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
     * Find all meals from Mensa am Elsterbecken
     *
     * @return All meals Mensa am Elsterbecken
     */
    @Override
    public Iterable<Meals_Mensa_am_Elsterbecken> findAll() {
        return meals_mensa_am_elsterbeckenRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa am Elsterbecken
     */
    @Override
    public List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_am_elsterbeckenRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa am Elsterbecken
     */
    @Override
    public List<Meals_Mensa_am_Elsterbecken> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_am_elsterbeckenRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to save
     * @param mensa The mensa to set
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
        meals_mensa_am_elsterbecken.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_am_elsterbecken.setAllergens(meal.getAllergens());
        meals_mensa_am_elsterbecken.setAdditives(meal.getAdditives());
        meals_mensa_am_elsterbecken.setRating(meal.getRating());
        meals_mensa_am_elsterbecken.setVotes(meal.getVotes());
        meals_mensa_am_elsterbecken.setStarsTotal(meal.getStarsTotal());

        Mensa_am_Elsterbecken mensa_am_elsterbecken = new Mensa_am_Elsterbecken();
        mensa_am_elsterbecken.setId(mensa.getId());
        mensa_am_elsterbecken.setName(mensa.getName());
        mensa_am_elsterbecken.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_elsterbecken.setMensa_am_elsterbecken(mensa_am_elsterbecken);

        meals_mensa_am_elsterbeckenRepository.save(meals_mensa_am_elsterbecken);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to delete
     * @param mensa The mensa to set
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
        meals_mensa_am_elsterbecken.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_am_elsterbecken.setAllergens(meal.getAllergens());
        meals_mensa_am_elsterbecken.setAdditives(meal.getAdditives());
        meals_mensa_am_elsterbecken.setRating(meal.getRating());
        meals_mensa_am_elsterbecken.setVotes(meal.getVotes());
        meals_mensa_am_elsterbecken.setStarsTotal(meal.getStarsTotal());

        Mensa_am_Elsterbecken mensa_am_elsterbecken = new Mensa_am_Elsterbecken();
        mensa_am_elsterbecken.setId(mensa.getId());
        mensa_am_elsterbecken.setName(mensa.getName());
        mensa_am_elsterbecken.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_elsterbecken.setMensa_am_elsterbecken(mensa_am_elsterbecken);

        meals_mensa_am_elsterbeckenRepository.delete(meals_mensa_am_elsterbecken);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa am Elsterbecken
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_am_elsterbeckenRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa am Elsterbecken
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_am_elsterbeckenRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa am Elsterbecken
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_am_elsterbeckenRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa am Elsterbecken
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_am_elsterbeckenRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa am Elsterbecken
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_am_elsterbeckenRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa am Elsterbecken
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_am_elsterbeckenRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }

}

