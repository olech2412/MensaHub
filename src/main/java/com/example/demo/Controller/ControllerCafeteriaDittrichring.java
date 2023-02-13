package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.entities.mensen.Cafeteria_Dittrichring;
import com.example.demo.JPA.services.meals.Meals_Cafeteria_DittrichringService;
import com.example.demo.JPA.services.meals.Meals_Mensa_Schoenauer_StrService;
import com.example.demo.JPA.services.mensen.Cafeteria_DittrichringService;
import com.example.demo.JPA.services.mensen.Mensa_Schoenauer_StrService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerCafeteriaDittrichring {

    private final Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService;

    private final Cafeteria_DittrichringService cafeteria_dittrichringService;



    public ControllerCafeteriaDittrichring(Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService, Cafeteria_DittrichringService cafeteria_dittrichringService) {
        this.meals_cafeteria_dittrichringService = meals_cafeteria_dittrichringService;
        this.cafeteria_dittrichringService = cafeteria_dittrichringService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/cafeteria_dittrichring")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_cafeteria_dittrichringService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/cafeteria_dittrichring")
    public void saveMeal(@RequestBody Meals_Cafeteria_Dittrichring receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Cafeteria_Dittrichring mealFromDB = (Meals_Cafeteria_Dittrichring) meals_cafeteria_dittrichringService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_cafeteria_dittrichringService.save(mealFromDB, cafeteria_dittrichringService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }

}
