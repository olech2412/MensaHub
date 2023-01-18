package com.example.demo.Controller;

import com.example.demo.JPA.Meal;
import com.example.demo.JPA.repository.MealRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
