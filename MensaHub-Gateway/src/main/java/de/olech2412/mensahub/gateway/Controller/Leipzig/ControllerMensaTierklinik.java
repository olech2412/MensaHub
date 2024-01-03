package de.olech2412.mensahub.gateway.Controller.Leipzig;

import com.sun.istack.NotNull;
import de.mensahub.gateway.Controller.BasicMealController;
import de.mensahub.gateway.JPA.entities.meals.Generic_Meal;
import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_Tierklinik;
import de.mensahub.gateway.JPA.entities.mensen.Mensa;
import de.mensahub.gateway.JPA.services.meals.Meals_Mensa_TierklinikService;
import de.mensahub.gateway.JPA.services.mensen.Mensa_TierklinikService;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/mensa_tierklinik")
@CrossOrigin(origins = "*")
public class ControllerMensaTierklinik implements BasicMealController {

    private final Meals_Mensa_TierklinikService meals_mensa_tierklinikService;

    private final Mensa_TierklinikService mensa_tierklinikService;

    /**
     * Constructor for ControllerMensaTierklinik
     *
     * @param meals_mensa_tierklinikService Service for meals of the mensa
     * @param mensa_tierklinikService       Service for the mensa
     */
    public ControllerMensaTierklinik(Meals_Mensa_TierklinikService meals_mensa_tierklinikService, Mensa_TierklinikService mensa_tierklinikService) {
        this.meals_mensa_tierklinikService = meals_mensa_tierklinikService;
        this.mensa_tierklinikService = mensa_tierklinikService;
    }

    /**
     * Get the name of the mensa
     *
     * @return - the name of the mensa
     */
    @GetMapping("")
    @Cacheable("mensa_tierklinik")
    public Mensa getMensa() {
        log.debug("Mensa info requested");
        return mensa_tierklinikService.getMensa();
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
        return meals_mensa_tierklinikService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
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
        return meals_mensa_tierklinikService.findAllMealsByServingDate(LocalDate.parse(servingDate));
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
        return meals_mensa_tierklinikService.findAllByCategory(category);
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
        return meals_mensa_tierklinikService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
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
        return meals_mensa_tierklinikService.findAllByRatingLessThanEqual(rating);
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
        return meals_mensa_tierklinikService.findAllByRatingGreaterThanEqual(rating);
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
        Meals_Mensa_Tierklinik mealFromDB = (Meals_Mensa_Tierklinik) meals_mensa_tierklinikService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_tierklinikService.save(mealFromDB, mensa_tierklinikService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
