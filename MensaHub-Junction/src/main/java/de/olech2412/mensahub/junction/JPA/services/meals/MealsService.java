package de.olech2412.mensahub.junction.JPA.services.meals;

import de.olech2412.mensahub.junction.JPA.repository.meals.MealRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class MealsService extends Meal {

    @Autowired
    MealRepository mealRepository;

    /**
     * @return Meals Menseria am Botanischen Garten
     */
    public List<Meal> findAll() {
        return mealRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Menseria am Botanischen Garten
     */
    public List<Meal> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate, Mensa mensa) {
        return mealRepository.findAllMealsByServingDateGreaterThanEqualAndMensa(servingDate, mensa);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Menseria am Botanischen Garten
     */
    public List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa) {
        return mealRepository.findAllMealsByServingDateAndMensa(servingDate, mensa);
    }

    /**
     * @param meal
     * @param mensa
     */
    public void save(Meal meal, Mensa mensa) {
        Meal mealToSave = new Meal();
        mealToSave.setId(meal.getId());
        mealToSave.setName(meal.getName());
        mealToSave.setCategory(meal.getCategory());
        mealToSave.setPrice(meal.getPrice());
        mealToSave.setServingDate(meal.getServingDate());
        mealToSave.setDescription(meal.getDescription());
        mealToSave.setRating(meal.getRating());
        mealToSave.setVotes(meal.getVotes());
        mealToSave.setStarsTotal(meal.getStarsTotal());
        mealToSave.setAllergens(meal.getAllergens());
        mealToSave.setMensa(mensa);

        mealRepository.save(mealToSave);
    }

    /**
     * @param meal
     * @param mensa
     */
    public void delete(Meal meal, Mensa mensa) {
        Meal mealToDelete = new Meal();
        mealToDelete.setId(meal.getId());
        mealToDelete.setName(meal.getName());
        mealToDelete.setCategory(meal.getCategory());
        mealToDelete.setPrice(meal.getPrice());
        mealToDelete.setServingDate(meal.getServingDate());
        mealToDelete.setDescription(meal.getDescription());
        mealToDelete.setRating(meal.getRating());
        mealToDelete.setVotes(meal.getVotes());
        mealToDelete.setStarsTotal(meal.getStarsTotal());
        mealToDelete.setAllergens(meal.getAllergens());
        mealToDelete.setMensa(mensa);

        mealRepository.delete(mealToDelete);
        log.warn("Meal deleted: {} from {}", meal.getName(), mensa.getName());
    }

}

