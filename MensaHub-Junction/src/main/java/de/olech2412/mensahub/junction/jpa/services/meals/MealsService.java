package de.olech2412.mensahub.junction.jpa.services.meals;

import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.meals.MealRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
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

    @Autowired
    ErrorEntityRepository errorEntityRepository;

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

    public Result<List<Meal>, JPAError> findTop20DistinctMealsExcludingGoudaForSubscribedMensa(Long userId) {
        try {
            List<Meal> meals = mealRepository.findTop20DistinctMealsExcludingGoudaByUser(userId);
            return Result.success(meals);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Meal>, JPAError> result = Result.error(new JPAError("Fehler beim lesen der Top 20 Gerichte exklusive Gouda: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<String>, JPAError> findAllDistinctCategories() {
        try {
            List<String> categories = mealRepository.findAllDistinctCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<String>, JPAError> result = Result.error(new JPAError("Fehler beim lesen der unterschiedlichen Kategorien: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<String>, JPAError> findAllUniqueAllergens() {
        try {
            List<String> allergens = mealRepository.findAllUniqueAllergens();
            allergens.remove(Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign")); // remove the N/A sign
            return Result.success(allergens);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<String>, JPAError> result = Result.error(new JPAError("Fehler beim lesen der unterschiedlichen Allergene: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

}

