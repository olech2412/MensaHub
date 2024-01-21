package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Medizincampus;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_am_MedizincampusRepository extends ListCrudRepository<Meals_Mensa_am_Medizincampus, Long> {

    List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDate(LocalDate servingDate);

    void deleteAllByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Medizincampus> findMeals_Mensa_am_MedizincampusByNameAndServingDateBeforeOrderByServingDateDesc(String name, LocalDate servingDate);


}