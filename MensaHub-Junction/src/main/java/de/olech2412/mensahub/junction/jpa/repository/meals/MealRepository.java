package de.olech2412.mensahub.junction.jpa.repository.meals;

import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends ListCrudRepository<Meal, Long> {

    List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa);

    @Query("SELECT DISTINCT category FROM Meal")
    List<String> findAllDistinctCategories();


    /**
     * This is required to remove the splits
     *
     * @return a list with all allergens
     */
    @Query(value = "WITH SplitAllergens AS ( " +
            "SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(m.allergens, ',', numbers.n), ',', -1)) AS allergen " +
            "FROM (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 " +
            "UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 " +
            "UNION ALL SELECT 9 UNION ALL SELECT 10) numbers " +
            "INNER JOIN mensaHub.meals m ON CHAR_LENGTH(m.allergens) - CHAR_LENGTH(REPLACE(m.allergens, ',', '')) >= numbers.n - 1 " +
            ") " +
            "SELECT DISTINCT allergen " +
            "FROM SplitAllergens " +
            "WHERE allergen <> '' " +
            "ORDER BY allergen",
            nativeQuery = true)
    List<String> findAllUniqueAllergens();

}