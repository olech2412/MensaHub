package com.essensGetter.api.Controller;

import com.essensGetter.api.JPA.entities.meals.Meal;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;
import com.essensGetter.api.JPA.services.meals.Meals_Mensa_am_ElsterbeckenService;
import com.essensGetter.api.JPA.services.mensen.Mensa_am_ElsterbeckenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMensaamElsterbecken {

    private final Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService;

    private final Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService;



    public ControllerMensaamElsterbecken(Meals_Mensa_am_ElsterbeckenService meals_mensa_am_elsterbeckenService, Mensa_am_ElsterbeckenService mensa_am_elsterbeckenService) {
        this.meals_mensa_am_elsterbeckenService = meals_mensa_am_elsterbeckenService;
        this.mensa_am_elsterbeckenService = mensa_am_elsterbeckenService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/mensa_am_elsterbecken")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_am_elsterbeckenService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/mensa_am_elsterbecken")
    public void saveMeal(@RequestBody Meals_Mensa_am_Elsterbecken receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Mensa_am_Elsterbecken mealFromDB = (Meals_Mensa_am_Elsterbecken) meals_mensa_am_elsterbeckenService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_am_elsterbeckenService.save(mealFromDB, mensa_am_elsterbeckenService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }
}
