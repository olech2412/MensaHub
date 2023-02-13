package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Mensa_am_Medizincampus;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.entities.mensen.Mensa_am_Medizincampus;
import com.example.demo.JPA.services.meals.Meals_Mensa_AcademicaService;
import com.example.demo.JPA.services.meals.Meals_Mensa_am_MedizincampusService;
import com.example.demo.JPA.services.mensen.Mensa_AcademicaService;
import com.example.demo.JPA.services.mensen.Mensa_am_MedizincampusService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMensaamMedizincampus {

    private final Meals_Mensa_am_MedizincampusService meals_mensa_am_medizincampusService;

    private final Mensa_am_MedizincampusService mensa_am_medizincampusService;



    public ControllerMensaamMedizincampus(Meals_Mensa_am_MedizincampusService meals_mensa_am_medizincampusService, Mensa_am_MedizincampusService mensa_am_medizincampusService) {
        this.meals_mensa_am_medizincampusService = meals_mensa_am_medizincampusService;
        this.mensa_am_medizincampusService = mensa_am_medizincampusService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/meals_mensa_am_medizincampus")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_am_medizincampusService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/mensa_am_medizincampus")
    public void saveMeal(@RequestBody Meals_Mensa_am_Medizincampus receivedMeal) {
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
