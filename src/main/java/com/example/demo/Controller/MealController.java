package com.example.demo.Controller;

import com.example.demo.JPA.Meal;
import com.example.demo.JPA.repository.MealRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

@RestController
@Log4j2
public class MealController {

    private final MealRepository mealRepository;

    public MealController(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    @GetMapping("/mealsForFritz")
    public Iterable<Meal> getMeals() {
        log.debug("Meals were requested");
        return mealRepository.findAllByServingDateGreaterThanEqual(LocalDate.now().minusDays(2));
    }

    @PostMapping("/mealsFromFritz")
    @Transactional
    public void saveMeal(@RequestBody Meal receivedMeal) {
        log.info("Meal received: " + receivedMeal);
        Meal mealFromDB = mealRepository.findByNameAndServingDateAndId(receivedMeal.getName(), receivedMeal.getServingDate(), receivedMeal.getId());
        if (mealFromDB != null) {
            mealFromDB.setVotes(mealFromDB.getVotes() + 1);
            mealFromDB.setStarsTotal((int) (mealFromDB.getStarsTotal() + receivedMeal.getRating()));
            Double calculatedRating =Double.valueOf(mealFromDB.getStarsTotal())/Double.valueOf(mealFromDB.getVotes());
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            mealFromDB.setRating(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")));
            mealRepository.save(mealFromDB);
        }else {
            log.error("Meal was not found in DB");
        }
    }

}
