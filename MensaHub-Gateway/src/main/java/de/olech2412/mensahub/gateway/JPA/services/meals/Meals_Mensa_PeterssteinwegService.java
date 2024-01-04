package de.olech2412.mensahub.gateway.JPA.services.meals;

import de.olech2412.mensahub.gateway.JPA.repository.meals.Meals_Mensa_PeterssteinwegRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Peterssteinweg;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Peterssteinweg;
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
     * Find all meals from Mensa Peterssteinweg
     *
     * @return Meals Mensa Peterssteinweg
     */
    @Override
    public Iterable<Meals_Mensa_Peterssteinweg> findAll() {
        return meals_mensa_peterssteinwegRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa Peterssteinweg
     */
    @Override
    public List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_peterssteinwegRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa Peterssteinwe
     */
    @Override
    public List<Meals_Mensa_Peterssteinweg> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_peterssteinwegRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
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
        meals_mensa_peterssteinweg.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_peterssteinweg.setAllergens(meal.getAllergens());
        meals_mensa_peterssteinweg.setAdditives(meal.getAdditives());
        meals_mensa_peterssteinweg.setRating(meal.getRating());
        meals_mensa_peterssteinweg.setVotes(meal.getVotes());
        meals_mensa_peterssteinweg.setStarsTotal(meal.getStarsTotal());

        Mensa_Peterssteinweg mensa_peterssteinweg = new Mensa_Peterssteinweg();
        mensa_peterssteinweg.setId(mensa.getId());
        mensa_peterssteinweg.setName(mensa.getName());
        mensa_peterssteinweg.setApiUrl(mensa.getApiUrl());

        meals_mensa_peterssteinweg.setMensa_peterssteinweg(mensa_peterssteinweg);

        meals_mensa_peterssteinwegRepository.save(meals_mensa_peterssteinweg);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
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
        meals_mensa_peterssteinweg.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_peterssteinweg.setAllergens(meal.getAllergens());
        meals_mensa_peterssteinweg.setAdditives(meal.getAdditives());
        meals_mensa_peterssteinweg.setRating(meal.getRating());
        meals_mensa_peterssteinweg.setVotes(meal.getVotes());
        meals_mensa_peterssteinweg.setStarsTotal(meal.getStarsTotal());

        Mensa_Peterssteinweg mensa_peterssteinweg = new Mensa_Peterssteinweg();
        mensa_peterssteinweg.setId(mensa.getId());
        mensa_peterssteinweg.setName(mensa.getName());
        mensa_peterssteinweg.setApiUrl(mensa.getApiUrl());

        meals_mensa_peterssteinweg.setMensa_peterssteinweg(mensa_peterssteinweg);

        meals_mensa_peterssteinwegRepository.delete(meals_mensa_peterssteinweg);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa Peterssteinweg
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_peterssteinwegRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa Peterssteinweg
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_peterssteinwegRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa Peterssteinweg
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_peterssteinwegRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa Peterssteinweg
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_peterssteinwegRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa Peterssteinweg
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_peterssteinwegRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa Peterssteinweg
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_peterssteinwegRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }
}

