package de.olech2412.mensahub.gateway.jpa.services.meals;

import de.olech2412.mensahub.gateway.jpa.repository.meals.MealsRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class MealsService {

    @Autowired
    MealsRepository mealsRepository;

    /**
     * Find all meals by serving date greater than or equal to servingDate
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date
     */
    public List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa) {
        return mealsRepository.findAllMealsByServingDateGreaterThanEqualAndMensa(servingDate, mensa);
    }

    /**
     * Find all meals by serving date
     *
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa am Botanischen Garten
     */
    public List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa) {
        return mealsRepository.findAllMealsByServingDateAndMensa(servingDate, mensa);
    }

    /**
     * Save a meal into the database and set the mensa
     *
     * @param meal  The meal to be saved
     * @param mensa The mensa the meal is from
     */
    public void save(Meal meal, Mensa mensa) {
        Meal mealToSave = new Meal();
        mealToSave.setId(meal.getId());
        mealToSave.setName(meal.getName());
        mealToSave.setCategory(meal.getCategory());
        mealToSave.setPrice(meal.getPrice());
        mealToSave.setServingDate(meal.getServingDate());
        mealToSave.setDescription(meal.getDescription());
        mealToSave.setAllergens(meal.getAllergens());
        mealToSave.setRating(meal.getRating());
        mealToSave.setVotes(meal.getVotes());
        mealToSave.setStarsTotal(meal.getStarsTotal());
        mealToSave.setMensa(mensa);

        mealsRepository.save(mealToSave);
    }

    /**
     * Delete a meal from the database
     *
     * @param meal  The meal to be deleted
     * @param mensa The mensa the meal is from
     */
    public void delete(Meal meal, Mensa mensa) {
        Meal mealToSave = new Meal();
        mealToSave.setId(meal.getId());
        mealToSave.setName(meal.getName());
        mealToSave.setCategory(meal.getCategory());
        mealToSave.setPrice(meal.getPrice());
        mealToSave.setServingDate(meal.getServingDate());
        mealToSave.setDescription(meal.getDescription());
        mealToSave.setAllergens(meal.getAllergens());
        mealToSave.setRating(meal.getRating());
        mealToSave.setVotes(meal.getVotes());
        mealToSave.setStarsTotal(meal.getStarsTotal());
        mealToSave.setMensa(mensa);

        mealsRepository.delete(mealToSave);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     *
     * @param name        The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id          The id of the meal
     * @return All meals by name and serving date and id from Mensa am Botanischen Garten
     */
    public List<? extends Meal> findByNameAndServingDateAndIdAndMensa(String name, LocalDate servingDate, Long id, Mensa mensa) {
        return mealsRepository.findByNameAndServingDateAndIdAndMensa(name, servingDate, id, mensa);
    }

    /**
     * Find all meals by category
     *
     * @param category The category of the meal
     * @return All meals by category from Mensa am Botanischen Garten
     */
    public List<? extends Meal> findAllByCategoryAndMensa(String category, Mensa mensa) {
        return mealsRepository.findAllByCategoryAndMensa(category, mensa);
    }

    /**
     * Find all meals by category and serving date
     *
     * @param category    The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa am Botanischen Garten
     */
    public List<? extends Meal> findAllByCategoryAndServingDateAndMensa(String category, LocalDate servingDate, Mensa mensa) {
        return mealsRepository.findAllByCategoryAndServingDateAndMensa(category, servingDate, mensa);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa am Botanischen Garten
     */
    public List<? extends Meal> findAllByRatingLessThanEqualAndMensa(Double rating, Mensa mensa) {
        return mealsRepository.findAllByRatingLessThanEqualAndMensa(rating, mensa);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     *
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa am Botanischen Garten
     */
    public List<? extends Meal> findAllByRatingGreaterThanEqualAndMensa(Double rating, Mensa mensa) {
        return mealsRepository.findAllByRatingGreaterThanEqualAndMensa(rating, mensa);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     *
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate   endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa am Botanischen Garten
     */
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualAndMensaOrderByServingDate(LocalDate startDate, LocalDate endDate, Mensa mensa) {
        return mealsRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualAndMensaOrderByServingDate(startDate, endDate, mensa);
    }

}

