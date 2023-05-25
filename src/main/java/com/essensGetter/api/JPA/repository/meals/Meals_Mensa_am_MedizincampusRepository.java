package com.essensGetter.api.JPA.repository.meals;

import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Medizincampus;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface Meals_Mensa_am_MedizincampusRepository extends CrudRepository<Meals_Mensa_am_Medizincampus, Long> {

    List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate);

    List<Meals_Mensa_am_Medizincampus> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(LocalDate startDate, LocalDate endDate);

    List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDate(LocalDate servingDate);

    List<Meals_Mensa_am_Medizincampus> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id);

    List<Meals_Mensa_am_Medizincampus> findAllByCategory(String category);

    List<Meals_Mensa_am_Medizincampus> findAllByCategoryAndServingDate(String category, LocalDate servingDate);

    List<Meals_Mensa_am_Medizincampus> findAllByRatingLessThanEqual(Double rating);

    List<Meals_Mensa_am_Medizincampus> findAllByRatingGreaterThanEqual(Double rating);

}