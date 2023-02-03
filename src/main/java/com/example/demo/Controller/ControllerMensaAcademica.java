package com.example.demo.Controller;

import com.example.demo.JPA.entities.meals.Meal;
import com.example.demo.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.example.demo.JPA.entities.meals.Meals_Mensa_Academica;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import com.example.demo.JPA.entities.mensen.Cafeteria_Dittrichring;
import com.example.demo.JPA.repository.mensen.Mensa_AcademicaRepository;
import com.example.demo.JPA.services.meals.Meals_Cafeteria_DittrichringService;
import com.example.demo.JPA.services.meals.Meals_Mensa_AcademicaService;
import com.example.demo.JPA.services.meals.Meals_Mensa_Schoenauer_StrService;
import com.example.demo.JPA.services.mensen.Cafeteria_DittrichringService;
import com.example.demo.JPA.services.mensen.Mensa_AcademicaService;
import com.example.demo.JPA.services.mensen.Mensa_Schoenauer_StrService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class ControllerMensaAcademica {

    private final Meals_Mensa_AcademicaService meals_mensa_academicaService;

    private final Mensa_AcademicaService mensa_academicaService;



    public ControllerMensaAcademica(Meals_Mensa_AcademicaService meals_mensa_academicaService, Mensa_AcademicaService mensa_academicaService) {
        this.meals_mensa_academicaService = meals_mensa_academicaService;
        this.mensa_academicaService = mensa_academicaService;
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @GetMapping("/mealsForFritz/mensa_academica")
    public Iterable<? extends Meal> getMeals() {
        log.debug("Meals were requested");
        return meals_mensa_academicaService.findAllMealsByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @CrossOrigin(origins = {"https://mensi-mates.whosfritz.de/"})
    @PostMapping("/mealsFromFritz/mensa_academica")
    public void saveMeal(@RequestBody Meals_Schoenauer_Str receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meals_Schoenauer_Str mealFromDB = (Meals_Schoenauer_Str) meals_mensa_academicaService.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId()).get(0);
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating = Double.valueOf(mealFromDB.getStarsTotal()) / Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            meals_mensa_academicaService.save(mealFromDB, mensa_academicaService.getMensa());
        } else {
            log.error("Meal was not found in DB");
        }
    }

}

