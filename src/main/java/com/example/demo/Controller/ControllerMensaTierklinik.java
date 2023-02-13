package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Mensa_Tierklinik;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.services.meals.Meals_Mensa_AcademicaService;
import com.example.demo.JPA.services.meals.Meals_Mensa_TierklinikService;
import com.example.demo.JPA.services.mensen.Mensa_AcademicaService;
import com.example.demo.JPA.services.mensen.Mensa_TierklinikService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMensaTierklinik {

    private final Meals_Mensa_TierklinikService meals_mensa_tierklinikService;

    private final Mensa_TierklinikService mensa_tierklinikService;



    public ControllerMensaTierklinik(Meals_Mensa_TierklinikService meals_mensa_tierklinikService, Mensa_TierklinikService mensa_tierklinikService) {
        this.meals_mensa_tierklinikService = meals_mensa_tierklinikService;
        this.mensa_tierklinikService = mensa_tierklinikService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/mensa_tierklinik")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_tierklinikService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/mensa_tierklinik")
    public void saveMeal(@RequestBody Meals_Schoenauer_Str receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Schoenauer_Str mealFromDB = (Meals_Schoenauer_Str) meals_mensa_tierklinikService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
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
