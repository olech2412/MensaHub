package de.olech2412.mensahub.gateway.JPA.services.meals;

import de.olech2412.mensahub.gateway.JPA.repository.meals.Meals_Mensa_TierklinikRepository;
import de.olech2412.mensahub.models.Leipzig.meals.Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Tierklinik;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Tierklinik;
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
     * Find all meals from Mensa Tierklinik
     *
     * @return Meals Mensa Tierklinik
     */
    @Override
    public Iterable<Meals_Mensa_Tierklinik> findAll() {
        return meals_mensa_tierklinikRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa Tierklinik
     */
    @Override
    public List<Meals_Mensa_Tierklinik> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_tierklinikRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa Tierklinik
     */
    @Override
    public List<Meals_Mensa_Tierklinik> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_tierklinikRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
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
        meals_mensa_tierklinik.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_tierklinik.setAllergens(meal.getAllergens());
        meals_mensa_tierklinik.setAdditives(meal.getAdditives());
        meals_mensa_tierklinik.setRating(meal.getRating());
        meals_mensa_tierklinik.setVotes(meal.getVotes());
        meals_mensa_tierklinik.setStarsTotal(meal.getStarsTotal());

        Mensa_Tierklinik mensa_tierklinik = new Mensa_Tierklinik();
        mensa_tierklinik.setId(mensa.getId());
        mensa_tierklinik.setName(mensa.getName());
        mensa_tierklinik.setApiUrl(mensa.getApiUrl());

        meals_mensa_tierklinik.setMensa_tierklinik(mensa_tierklinik);

        meals_mensa_tierklinikRepository.save(meals_mensa_tierklinik);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
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
        meals_mensa_tierklinik.setAdditionalInfo(meal.getAdditionalInfo());
        meals_mensa_tierklinik.setAllergens(meal.getAllergens());
        meals_mensa_tierklinik.setAdditives(meal.getAdditives());
        meals_mensa_tierklinik.setRating(meal.getRating());
        meals_mensa_tierklinik.setVotes(meal.getVotes());
        meals_mensa_tierklinik.setStarsTotal(meal.getStarsTotal());

        Mensa_Tierklinik mensa_tierklinik = new Mensa_Tierklinik();
        mensa_tierklinik.setId(mensa.getId());
        mensa_tierklinik.setName(mensa.getName());
        mensa_tierklinik.setApiUrl(mensa.getApiUrl());

        meals_mensa_tierklinik.setMensa_tierklinik(mensa_tierklinik);

        meals_mensa_tierklinikRepository.delete(meals_mensa_tierklinik);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa Tierklinik
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_mensa_tierklinikRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa Tierklinik
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_mensa_tierklinikRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa Tierklinik
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_mensa_tierklinikRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa Tierklinik
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_mensa_tierklinikRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa Tierklinik
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_mensa_tierklinikRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa Tierklinik
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_mensa_tierklinikRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }
}

