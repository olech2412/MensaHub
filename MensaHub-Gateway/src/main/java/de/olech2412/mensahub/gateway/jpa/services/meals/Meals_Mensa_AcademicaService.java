package de.olech2412.mensahub.gateway.jpa.services.meals;

import de.olech2412.mensahub.gateway.jpa.repository.meals.Meals_Mensa_AcademicaRepository;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Academica;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Academica;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
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

    /**
     * Find all meals from Mensa Academica
     *
     * @return All meals Mensa Academica
     */
    @Override
    public List<Meals_Mensa_Academica> findAll() {
        return meals_mensa_academicaRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa Academica
     */
    @Override
    public List<Meals_Mensa_Academica> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_academicaRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa Academica
     */
    @Override
    public List<Meals_Mensa_Academica> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_academicaRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
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
        meals_mensa_academica.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_academica.setAllergens(meal.getAllergens());
        meals_mensa_academica.setAdditives(meal.getAdditives());
        meals_mensa_academica.setRating(meal.getRating());
        meals_mensa_academica.setVotes(meal.getVotes());
        meals_mensa_academica.setStarsTotal(meal.getStarsTotal());

        Mensa_Academica mensa_academica = new Mensa_Academica();
        mensa_academica.setId(mensa.getId());
        mensa_academica.setName(mensa.getName());
        mensa_academica.setApiUrl(mensa.getApiUrl());

        meals_mensa_academica.setMensa_academica(mensa_academica);

        meals_mensa_academicaRepository.save(meals_mensa_academica);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
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
        meals_mensa_academica.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_academica.setAllergens(meal.getAllergens());
        meals_mensa_academica.setAdditives(meal.getAdditives());
        meals_mensa_academica.setRating(meal.getRating());
        meals_mensa_academica.setVotes(meal.getVotes());
        meals_mensa_academica.setStarsTotal(meal.getStarsTotal());

        Mensa_Academica mensa_academica = new Mensa_Academica();
        mensa_academica.setId(mensa.getId());
        mensa_academica.setName(mensa.getName());
        mensa_academica.setApiUrl(mensa.getApiUrl());

        meals_mensa_academica.setMensa_academica(mensa_academica);

        meals_mensa_academicaRepository.delete(meals_mensa_academica);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa Academica
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_academicaRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_academicaRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_academicaRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_academicaRepository.findAllByRatingLessThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_academicaRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_academicaRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }


}

