package de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.meals;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.meals.MealsRepository;
import de.olech2412.mensahub.datadispatcher.jpa.repository.RatingRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Rating;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@Import({Meal.class})
public class MealsService {

    @Autowired
    MealsRepository meals_Repository;

    @Autowired
    RatingRepository ratingRepository;


    /**
     * @return Meals Cafeteria Dittrichring
     */
    public List<Meal> findAll() {
        return meals_Repository.findAll();
    }

    /**
     * @param servingDate The serving date
     * @return All meals by serving date greater than or equal to serving date from Cafeteria Dittrichring
     */
    public List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa) {
        return meals_Repository.findAllMealsByServingDateGreaterThanEqualAndMensa(servingDate, mensa);
    }

    /**
     * @param servingDate The serving date
     * @return All meals by serving date from Cafeteria Dittrichring
     */
    public List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa) {
        return meals_Repository.findAllMealsByServingDateAndMensa(servingDate, mensa);
    }

    /**
     * @param meal  The meal
     * @param mensa The mensa
     */
    public void save(Meal meal, Mensa mensa) {

        meal.setMensa(mensa);

        meals_Repository.save(meal);
    }

    @Transactional
    public void saveAll(List<Meal> meals, Mensa mensa) {
        for (Meal meal : meals) {
            meal.setMensa(mensa);
        }

        meals_Repository.saveAll(meals);
    }

    @Transactional
    public HashMap<Rating, Meal> deleteAllByServingDate(LocalDate servingDate, Mensa mensa) {
        List<Meal> meals = meals_Repository.findAllByServingDateAndMensa(servingDate, mensa);
        HashMap<Rating, Meal> deletedRatings = new HashMap<>();
        for (Meal meal : meals) {
            Optional<Rating> ratingOptional = ratingRepository.findByMealId(meal.getId());
            if (ratingOptional.isPresent()) {
                deletedRatings.put(ratingOptional.get(), meal);
                Rating unchainedRating = ratingOptional.get();
                unchainedRating.setMeal(null);
                ratingRepository.save(unchainedRating);
                log.info("Unchained rating for meal {} for mensa {} from user {}", meal.getName(), mensa.getName(), ratingOptional.get().getMailUser().getEmail());
            }
            meals_Repository.delete(meal);
        }
        return deletedRatings;
    }

    /**
     * @param meal  The meal
     * @param mensa The mensa
     */
    public void delete(Meal meal, Mensa mensa) {
        meal.setMensa(mensa);

        meals_Repository.delete(meal);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    public List<Meal> findAllByNameAndMensaAndServingDateBeforeOrderByServingDateDesc(String name, Mensa mensa, LocalDate servingDate) {
        return meals_Repository.findAllByNameAndMensaAndServingDateBeforeOrderByServingDateDesc(name, mensa, servingDate);
    }
}

