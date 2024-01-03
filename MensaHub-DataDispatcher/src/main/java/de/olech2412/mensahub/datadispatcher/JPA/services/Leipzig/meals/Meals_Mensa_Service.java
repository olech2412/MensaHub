package de.olech2412.mensahub.datadispatcher.JPA.services.Leipzig.meals;

import de.olech2412.mensahub.datadispatcher.JPA.services.Abstract_Service;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public abstract class Meals_Mensa_Service extends Abstract_Service {

    @Override
    public abstract Iterable<? extends Meal> findAll();

    public abstract List<? extends Meal> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    public abstract List<? extends Meal> findAllMealsByServingDate(LocalDate servingDate);

    public abstract void save(Meal meal, Mensa mensa);

    public abstract void saveAll(List<? extends Meal> meals, Mensa mensa);

    public abstract void deleteAllByServingDate(LocalDate servingDate);

    public abstract void delete(Meal meal, Mensa mensa);

    public abstract List<? extends Meal> findMealsFromMensaByNameAndServingDateBeforeOrderByServingDateDesc(String name, LocalDate servingDate);

}
