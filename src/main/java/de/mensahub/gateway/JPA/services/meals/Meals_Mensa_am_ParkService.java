package de.mensahub.gateway.JPA.services.meals;

import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Park;
import de.mensahub.gateway.JPA.entities.mensen.Mensa;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Park;
import de.mensahub.gateway.JPA.repository.meals.Meals_Mensa_am_ParkRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_am_ParkService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_am_ParkRepository meals_mensa_am_parkRepository;

    /**
     * Find all meals from Mensa am Park
     *
     * @return Meals Mensa am Park
     */
    @Override
    public Iterable<Meals_Mensa_am_Park> findAll() {
        return meals_mensa_am_parkRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa am Park
     */
    @Override
    public List<Meals_Mensa_am_Park> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_am_parkRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa am Park
     */
    @Override
    public List<Meals_Mensa_am_Park> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_am_parkRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Park meals_mensa_am_park = new Meals_Mensa_am_Park();
        meals_mensa_am_park.setId(meal.getId());
        meals_mensa_am_park.setName(meal.getName());
        meals_mensa_am_park.setCategory(meal.getCategory());
        meals_mensa_am_park.setPrice(meal.getPrice());
        meals_mensa_am_park.setServingDate(meal.getServingDate());
        meals_mensa_am_park.setDescription(meal.getDescription());
        meals_mensa_am_park.setRating(meal.getRating());
        meals_mensa_am_park.setVotes(meal.getVotes());
        meals_mensa_am_park.setStarsTotal(meal.getStarsTotal());
        meals_mensa_am_park.setResponseCode(meal.getResponseCode());

        Mensa_am_Park mensa_am_park = new Mensa_am_Park();
        mensa_am_park.setId(mensa.getId());
        mensa_am_park.setName(mensa.getName());
        mensa_am_park.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_park.setMensa_am_park(mensa_am_park);

        meals_mensa_am_parkRepository.save(meals_mensa_am_park);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Park meals_mensa_am_park = new Meals_Mensa_am_Park();
        meals_mensa_am_park.setId(meal.getId());
        meals_mensa_am_park.setName(meal.getName());
        meals_mensa_am_park.setCategory(meal.getCategory());
        meals_mensa_am_park.setPrice(meal.getPrice());
        meals_mensa_am_park.setServingDate(meal.getServingDate());
        meals_mensa_am_park.setDescription(meal.getDescription());
        meals_mensa_am_park.setRating(meal.getRating());
        meals_mensa_am_park.setVotes(meal.getVotes());
        meals_mensa_am_park.setStarsTotal(meal.getStarsTotal());
        meals_mensa_am_park.setResponseCode(meal.getResponseCode());

        Mensa_am_Park mensa_am_park = new Mensa_am_Park();
        mensa_am_park.setId(mensa.getId());
        mensa_am_park.setName(mensa.getName());
        mensa_am_park.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_park.setMensa_am_park(mensa_am_park);

        meals_mensa_am_parkRepository.delete(meals_mensa_am_park);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa am Park
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_am_parkRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa am Park
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_am_parkRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa am Park
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_am_parkRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa am Park
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_am_parkRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa am Park
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_am_parkRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa am Park
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_am_parkRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }

}

