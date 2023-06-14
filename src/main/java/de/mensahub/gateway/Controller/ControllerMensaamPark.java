package de.mensahub.gateway.Controller;

import de.mensahub.gateway.JPA.entities.meals.Generic_Meal;
import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Park;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Park;
import de.mensahub.gateway.JPA.services.meals.Meals_Mensa_am_ParkService;
import de.mensahub.gateway.JPA.services.mensen.Mensa_am_ParkService;
import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
@RequestMapping("/mensa_am_park")
@CrossOrigin(origins = "https://mensi-mates.whosfritz.de")
public class ControllerMensaamPark {

    private final Meals_Mensa_am_ParkService meals_mensa_am_parkService;

    private final Mensa_am_ParkService mensa_am_parkService;

    /**
     * Constructor for ControllerMensaamPark
     * @param meals_mensa_am_parkService Service for meals of the mensa
     * @param mensa_am_parkService Service for the mensa
     */
    public ControllerMensaamPark(Meals_Mensa_am_ParkService meals_mensa_am_parkService, Mensa_am_ParkService mensa_am_parkService) {
        this.meals_mensa_am_parkService = meals_mensa_am_parkService;
        this.mensa_am_parkService = mensa_am_parkService;
    }

    /**
     * Get the name of the mensa
     * @return - the name of the mensa
     */
    @GetMapping("")
    public Iterable<Mensa_am_Park> getMensa() {
        log.debug("Mensa info requested");
        return mensa_am_parkService.findAll();
    }

    /**
     * Get all meals of the mensa from startDate until endDate
     * @param startDate - the start date as string (format: yyyy-MM-dd)
     * @param enddate - the end date as string (format: yyyy-MM-dd)
     * @return - all meals between the start and end date
     */
    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_mensa_am_parkService.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    /**
     * Get all meals of the mensa for a specific date
     * @param servingDate - the date as string (format: yyyy-MM-dd)
     * @return - all meals with the specific date
     */
    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_mensa_am_parkService.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the mensa for a specific category
     * @param category - the category as string
     * @return - all meals with the specific category
     */
    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_mensa_am_parkService.findAllByCategory(category);
    }

    /**
     * Get all meals of the mensa for a specific category and date
     * @param category - the category as string
     * @param servingDate - the date as string (format: yyyy-MM-dd)
     * @return - all meals with the specific category and date
     */
    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_mensa_am_parkService.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    /**
     * Get all meals of the mensa where rating is less than a specific value
     * @param rating - the rating as double (specific value)
     * @return - all meals with rating less than the specific value
     */
    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_mensa_am_parkService.findAllByRatingLessThanEqual(rating);
    }

    /**
     * Get all meals of the mensa where rating is higher than a specific value
     * @param rating - the rating as double (specific value)
     * @return - all meals with rating higher than the specific value
     */
    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_mensa_am_parkService.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Receive a rating for a meal
     * A complete meal object is needed because it will be identified by name, servingDate and id
     * @param receivedMeal - the meal with the rating
     */
    @PostMapping("/sendRating")
    public void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Mensa_am_Park mealFromDB = (Meals_Mensa_am_Park) meals_mensa_am_parkService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_am_parkService.save(mealFromDB, mensa_am_parkService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
