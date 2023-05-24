package com.essensGetter.api.Controller;

import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Park;
import com.essensGetter.api.JPA.services.meals.Meals_Mensa_am_ParkService;
import com.essensGetter.api.JPA.services.mensen.Mensa_am_ParkService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMensaamPark {

    private final Meals_Mensa_am_ParkService meals_mensa_am_parkService;

    private final Mensa_am_ParkService mensa_am_parkService;



    public ControllerMensaamPark(Meals_Mensa_am_ParkService meals_mensa_am_parkService, Mensa_am_ParkService mensa_am_parkService) {
        this.meals_mensa_am_parkService = meals_mensa_am_parkService;
        this.mensa_am_parkService = mensa_am_parkService;
    }

    @GetMapping("/getMeals/mensa_am_park")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_am_parkService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @PostMapping("/sendRating/mensa_am_park")
    public void saveMeal(@RequestBody Meals_Mensa_am_Park receivedMeal) {
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
