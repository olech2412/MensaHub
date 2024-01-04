package de.olech2412.mensahub.gateway.JPA.services.meals;

import de.olech2412.mensahub.gateway.JPA.repository.meals.Meals_Mensa_am_MedizincampusRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Medizincampus;
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
     * Find all meals from Mensa am Medizincampus
     *
     * @return Meals Mensa am Medizincampus
     */
    @Override
    public Iterable<Meals_Mensa_am_Medizincampus> findAll() {
        return meals_mensa_am_medizincampusRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
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
        meals_mensa_am_medizincampus.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_am_medizincampus.setAllergens(meal.getAllergens());
        meals_mensa_am_medizincampus.setAdditives(meal.getAdditives());
        meals_mensa_am_medizincampus.setRating(meal.getRating());
        meals_mensa_am_medizincampus.setVotes(meal.getVotes());
        meals_mensa_am_medizincampus.setStarsTotal(meal.getStarsTotal());

        Mensa_am_Medizincampus mensa_am_medizincampus = new Mensa_am_Medizincampus();
        mensa_am_medizincampus.setId(mensa.getId());
        mensa_am_medizincampus.setName(mensa.getName());
        mensa_am_medizincampus.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_medizincampus.setMensa_am_medizincampus(mensa_am_medizincampus);

        meals_mensa_am_medizincampusRepository.save(meals_mensa_am_medizincampus);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
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
        meals_mensa_am_medizincampus.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_am_medizincampus.setAllergens(meal.getAllergens());
        meals_mensa_am_medizincampus.setAdditives(meal.getAdditives());
        meals_mensa_am_medizincampus.setRating(meal.getRating());
        meals_mensa_am_medizincampus.setVotes(meal.getVotes());
        meals_mensa_am_medizincampus.setStarsTotal(meal.getStarsTotal());

        Mensa_am_Medizincampus mensa_am_medizincampus = new Mensa_am_Medizincampus();
        mensa_am_medizincampus.setId(mensa.getId());
        mensa_am_medizincampus.setName(mensa.getName());
        mensa_am_medizincampus.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_medizincampus.setMensa_am_medizincampus(mensa_am_medizincampus);

        meals_mensa_am_medizincampusRepository.delete(meals_mensa_am_medizincampus);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa am Medizincampus
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_am_medizincampusRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa am Medizincampus
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_am_medizincampusRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa am Medizincampus
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa am Medizincampus
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_am_medizincampusRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa am Medizincampus
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_am_medizincampusRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa am Medizincampus
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_am_medizincampusRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }
}

