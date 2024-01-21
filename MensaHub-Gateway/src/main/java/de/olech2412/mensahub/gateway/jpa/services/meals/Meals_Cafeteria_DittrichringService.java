package de.olech2412.mensahub.gateway.jpa.services.meals;

import de.olech2412.mensahub.gateway.jpa.repository.meals.Meals_Cafeteria_DittrichringRepository;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Leipzig.mensen.Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Cafeteria_DittrichringService extends Meals_Mensa_Service {

    @Autowired
    Meals_Cafeteria_DittrichringRepository meals_cafeteria_dittrichringRepository;


    /**
     * Find all meals from Cafeteria Dittrichring
     *
     * @return All meals Cafeteria Dittrichring
     */
    @Override
    public List<Meals_Cafeteria_Dittrichring> findAll() {
        return meals_cafeteria_dittrichringRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa Academica
     */
    @Override
    public List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_cafeteria_dittrichringRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa Academica
     */
    @Override
    public List<Meals_Cafeteria_Dittrichring> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_cafeteria_dittrichringRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Cafeteria_Dittrichring meals_cafeteria_dittrichring = new Meals_Cafeteria_Dittrichring();
        meals_cafeteria_dittrichring.setId(meal.getId());
        meals_cafeteria_dittrichring.setName(meal.getName());
        meals_cafeteria_dittrichring.setCategory(meal.getCategory());
        meals_cafeteria_dittrichring.setPrice(meal.getPrice());
        meals_cafeteria_dittrichring.setServingDate(meal.getServingDate());
        meals_cafeteria_dittrichring.setDescription(meal.getDescription());
        meals_cafeteria_dittrichring.setAdditionalInfo(meal.getAdditionalInfo());
        meals_cafeteria_dittrichring.setAllergens(meal.getAllergens());
        meals_cafeteria_dittrichring.setAdditives(meal.getAdditives());
        meals_cafeteria_dittrichring.setRating(meal.getRating());
        meals_cafeteria_dittrichring.setVotes(meal.getVotes());
        meals_cafeteria_dittrichring.setStarsTotal(meal.getStarsTotal());

        Cafeteria_Dittrichring cafeteria_dittrichring = new Cafeteria_Dittrichring();
        cafeteria_dittrichring.setId(mensa.getId());
        cafeteria_dittrichring.setName(mensa.getName());
        cafeteria_dittrichring.setApiUrl(mensa.getApiUrl());

        meals_cafeteria_dittrichring.setCafeteria_dittrichring(cafeteria_dittrichring);

        meals_cafeteria_dittrichringRepository.save(meals_cafeteria_dittrichring);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Cafeteria_Dittrichring meals_cafeteria_dittrichring = new Meals_Cafeteria_Dittrichring();
        meals_cafeteria_dittrichring.setId(meal.getId());
        meals_cafeteria_dittrichring.setName(meal.getName());
        meals_cafeteria_dittrichring.setCategory(meal.getCategory());
        meals_cafeteria_dittrichring.setPrice(meal.getPrice());
        meals_cafeteria_dittrichring.setServingDate(meal.getServingDate());
        meals_cafeteria_dittrichring.setDescription(meal.getDescription());
        meals_cafeteria_dittrichring.setAdditionalInfo(meal.getAdditionalInfo());
        meals_cafeteria_dittrichring.setAllergens(meal.getAllergens());
        meals_cafeteria_dittrichring.setAdditives(meal.getAdditives());
        meals_cafeteria_dittrichring.setRating(meal.getRating());
        meals_cafeteria_dittrichring.setVotes(meal.getVotes());
        meals_cafeteria_dittrichring.setStarsTotal(meal.getStarsTotal());

        Cafeteria_Dittrichring cafeteria_dittrichring = new Cafeteria_Dittrichring();
        cafeteria_dittrichring.setId(mensa.getId());
        cafeteria_dittrichring.setName(mensa.getName());
        cafeteria_dittrichring.setApiUrl(mensa.getApiUrl());

        meals_cafeteria_dittrichring.setCafeteria_dittrichring(cafeteria_dittrichring);

        meals_cafeteria_dittrichringRepository.delete(meals_cafeteria_dittrichring);
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
        return meals_cafeteria_dittrichringRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_cafeteria_dittrichringRepository.findAllByCategory(category);
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
        return meals_cafeteria_dittrichringRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_cafeteria_dittrichringRepository.findAllByRatingLessThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa Academica
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_cafeteria_dittrichringRepository.findAllByRatingGreaterThanEqual(rating);
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
        return meals_cafeteria_dittrichringRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }


}

