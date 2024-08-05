package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.meals;

import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

@Import({Meal.class})
public interface MealsRepository extends ListCrudRepository<Meal, Long> {

    List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllByServingDateAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllByNameAndMensaAndServingDateBeforeOrderByServingDateDesc(String name, Mensa mensa, LocalDate servingDate);

}