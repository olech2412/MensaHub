package de.mensahub.gateway.Controller;

import de.mensahub.gateway.JPA.entities.meals.Generic_Meal;
import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Menseria_am_Botanischen_Garten;
import de.mensahub.gateway.JPA.entities.mensen.Menseria_am_Botanischen_Garten;
import de.mensahub.gateway.JPA.services.meals.Meals_Menseria_am_Botanischen_GartenServices;
import de.mensahub.gateway.JPA.services.mensen.Menseria_am_Botanischen_GartenService;
import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@CrossOrigin(origins = "https://mensi-mates.whosfritz.de")
@RestController
@Log4j2
@RequestMapping("/menseria_am_botanischen_garten")
public class ControllerMenseriaamBotanischenGarten {

    private final Meals_Menseria_am_Botanischen_GartenServices meals_menseria_am_botanischen_gartenServices;

    private final Menseria_am_Botanischen_GartenService menseria_am_botanischen_gartenService;


    public ControllerMenseriaamBotanischenGarten(Meals_Menseria_am_Botanischen_GartenServices meals_menseria_am_botanischen_gartenServices, Menseria_am_Botanischen_GartenService menseria_am_botanischen_gartenService) {
        this.meals_menseria_am_botanischen_gartenServices = meals_menseria_am_botanischen_gartenServices;
        this.menseria_am_botanischen_gartenService = menseria_am_botanischen_gartenService;
    }

    @GetMapping("")
    public Iterable<Menseria_am_Botanischen_Garten> getMensa() {
        log.debug("Mensa info requested");
        return menseria_am_botanischen_gartenService.findAll();
    }

    @GetMapping("/getMeals/from/{startDate}/to/{enddate}")
    public Iterable<? extends Meal> getMealsNextDays(@PathVariable String startDate, @PathVariable String enddate) {
        log.debug("Meals were requested from " + startDate + " until " + enddate);
        return meals_menseria_am_botanischen_gartenServices.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate.parse(startDate), LocalDate.parse(enddate));
    }

    @GetMapping("/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByServingDate(@PathVariable(value = "servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with servingDate: " + servingDate);
        return meals_menseria_am_botanischen_gartenServices.findAllMealsByServingDate(LocalDate.parse(servingDate));
    }

    @GetMapping("/category/{category}")
    public Iterable<? extends Meal> getMealByCategory(@PathVariable("category") @NotNull String category) {
        log.debug("Meals were requested with category: " + category);
        return meals_menseria_am_botanischen_gartenServices.findAllByCategory(category);
    }

    @GetMapping("/category/{category}/servingDate/{servingDate}")
    public Iterable<? extends Meal> getMealByCategoryAndServingDate(@PathVariable("category") @NotNull String category, @PathVariable("servingDate") @NotNull String servingDate) {
        log.debug("Meals were requested with category: " + category + " on " + servingDate);
        return meals_menseria_am_botanischen_gartenServices.findAllByCategoryAndServingDate(category, LocalDate.parse(servingDate));
    }

    @GetMapping("/byRatingLessThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingLessThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating less then: " + rating);
        return meals_menseria_am_botanischen_gartenServices.findAllByRatingLessThanEqual(rating);
    }

    @GetMapping("/byRatingHigherThen/{rating}")
    public Iterable<? extends Meal> getMealByRatingHigherThan(@PathVariable("rating") @NotNull Double rating) {
        log.debug("Meals were requested with rating higher then: " + rating);
        return meals_menseria_am_botanischen_gartenServices.findAllByRatingGreaterThanEqual(rating);
    }

    @PostMapping("/sendRating")
    public void saveRatingForMeal(@RequestBody Generic_Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Menseria_am_Botanischen_Garten mealFromDB = (Meals_Menseria_am_Botanischen_Garten) meals_menseria_am_botanischen_gartenServices.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_menseria_am_botanischen_gartenServices.save(mealFromDB, menseria_am_botanischen_gartenService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
