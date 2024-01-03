package de.olech2412.mensahub.gateway.Controller.Leipzig;

import com.sun.istack.NotNull;
import de.olech2412.mensahub.gateway.Controller.BasicMealController;
import de.olech2412.mensahub.gateway.JPA.services.meals.Meals_Mensa_am_MedizincampusService;
import de.olech2412.mensahub.gateway.JPA.services.mensen.Mensa_am_MedizincampusService;
import de.olech2412.mensahub.models.Generic_Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meal;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/mensa_am_medizincampus")
@CrossOrigin(origins = "*")
public class ControllerMensaamMedizincampus implements BasicMealController {

    private final Meals_Mensa_am_MedizincampusService meals_mensa_am_medizincampusService;

    private final Mensa_am_MedizincampusService mensa_am_medizincampusService;

    /**
     * Constructor for ControllerMensaamMedizincampus
     *
     * @param meals_mensa_am_medizincampusService Service for meals of the mensa
     * @param mensa_am_medizincampusService       Service for the mensa
     */
    public ControllerMensaamMedizincampus(Meals_Mensa_am_MedizincampusService meals_mensa_am_medizincampusService, Mensa_am_MedizincampusService mensa_am_medizincampusService) {
        this.meals_mensa_am_medizincampusService = meals_mensa_am_medizincampusService;
        this.mensa_am_medizincampusService = mensa_am_medizincampusService;
    }

    /**
     * Get the name of the mensa
     *
     * @return - the name of the mensa
     */
    @GetMapping("")
    @Cacheable("mensa_am_medizincampus")
    public Mensa getMensa() {
        log.debug("Mensa info requested");
        return mensa_am_medizincampusService.getMensa();
    }

    /**
     * Get all meals of the mensa from startDate until endDate
     *
     * @param startDate - the start date as string (format: yyyy-MM-dd)
     * @param enddate   - the end date as string (format: yyyy-MM-dd)
     * @return - all meals between the start and end date
     */
    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_mensa_am_medizincampusService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    /**
     * Get all meals of the mensa for a specific date
     *
     * @param servingDate - the date as string (format: yyyy-MM-dd)
     * @return - all meals with the specific date
     */
    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_mensa_am_medizincampusService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the mensa for a specific category
     *
     * @param category - the category as string
     * @return - all meals with the specific category
     */
    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_mensa_am_medizincampusService.findAllByCategory(category);
    }

    /**
     * Get all meals of the mensa for a specific category and date
     *
     * @param category    - the category as string
     * @param servingDate - the date as string (format: yyyy-MM-dd)
     * @return - all meals with the specific category and date
     */
    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_mensa_am_medizincampusService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the mensa where rating is less than a specific value
     *
     * @param rating - the rating as double (specific value)
     * @return - all meals with rating less than the specific value
     */
    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_mensa_am_medizincampusService.findAllByRatingLessThanEqual(rating);
    }

    /**
     * Get all meals of the mensa where rating is higher than a specific value
     *
     * @param rating - the rating as double (specific value)
     * @return - all meals with rating higher than the specific value
     */
    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_mensa_am_medizincampusService.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Receive a rating for a meal
     * A complete meal object is needed because it will be identified by name, servingDate and id
     *
     * @param receivedMeal - the meal with the rating
     */
    @PostMapping("/sendRating")
    public void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Mensa_am_Medizincampus mealFromDB = (Meals_Mensa_am_Medizincampus) meals_mensa_am_medizincampusService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_am_medizincampusService.save(mealFromDB, mensa_am_medizincampusService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
