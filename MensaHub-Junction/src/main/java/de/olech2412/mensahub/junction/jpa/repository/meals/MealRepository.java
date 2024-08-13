package de.olech2412.mensahub.junction.jpa.repository.meals;

import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends ListCrudRepository<Meal, Long> {

    List<Meal> findAllMealsByServingDateGreaterThanEqualAndMensa(LocalDate servingDate, Mensa mensa);

    List<Meal> findAllMealsByServingDateAndMensa(LocalDate servingDate, Mensa mensa);

    @Query("SELECT DISTINCT category FROM Meal")
    List<String> findAllDistinctCategories();


    /**
     * This is required to remove the splits
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

    @Query(value = "SELECT * FROM meals m WHERE m.name IN (SELECT DISTINCT name FROM meals) ORDER BY m.name ASC LIMIT 20", nativeQuery = true)
    List<Meal> findTop20DistinctMeals();

    /*
    I have the user id in my code. I now want to get the mensen the user is linked to and get the meals of the mensa meal tables, put them together and get from this combined table the top 20 meals by distinc meal name. the user id and the mensas are important. user id is parameter


    @Query(value = "SELECT * FROM meals m WHERE m.name IN (" +
            "  SELECT DISTINCT m1.name FROM meals m1 WHERE m1.mensa_id IN (" +
            "    SELECT mensas_id FROM mail_user_mensa_abbo WHERE mail_users_id = :userId" +
            "  )" +
            ") ORDER BY m.name ASC LIMIT 20", nativeQuery = true)
    List<Meal> findTop20DistinctMealsByUser(@Param("userId") Long userId);

     */

    @Query(value = "SELECT m.name FROM meals m WHERE m.name IN (SELECT DISTINCT m1.name FROM meals m1 WHERE m1.mensa_id IN (SELECT mensas_id FROM mail_user_mensa_abbo WHERE mail_users_id = :userId)) GROUP BY m.name ORDER BY Count(*) DESC LIMIT 20", nativeQuery = true)
    List<String> findTop20DistinctMealNamesByUser(@Param("userId") Long userId);


    @Query(value = "SELECT m.* " +
            "FROM meals m " +
            "WHERE m.name IN (" +
            "    SELECT DISTINCT m1.name " +
            "    FROM meals m1 " +
            "    WHERE m1.mensa_id IN (" +
            "        SELECT mensas_id " +
            "        FROM mail_user_mensa_abbo " +
            "        WHERE mail_users_id = :userId" +
            "    )" +
            ") " +
            "GROUP BY m.name " +
            "ORDER BY COUNT(*) DESC " +
            "LIMIT 20", nativeQuery = true)
    List<Meal> findTop20DistinctMealsByUser(@Param("userId") Long userId);

    @Query(value = "SELECT m.* " +
            "FROM meals m " +
            "WHERE m.name IN (" +
            "    SELECT DISTINCT m1.name " +
            "    FROM meals m1 " +
            "    WHERE m1.mensa_id IN (" +
            "        SELECT mensas_id " +
            "        FROM mail_user_mensa_abbo " +
            "        WHERE mail_users_id = :userId" +
            "    )" +
            ") " +
            "AND m.name NOT LIKE '%Geriebener Gouda%' " +
            "AND m.name NOT LIKE '%Geriebenem Gouda%' " +
            "AND m.name NOT LIKE '%geriebenem Gouda%' " +
            "AND m.name NOT LIKE '%Geriebenen Gouda%' " +
            "GROUP BY m.name " +
            "ORDER BY COUNT(*) DESC " +
            "LIMIT 20", nativeQuery = true)
    List<Meal> findTop20DistinctMealsExcludingGoudaByUser(@Param("userId") Long userId);
}
