package de.mensahub.gateway.JPA.services.meals;

import de.mensahub.gateway.JPA.entities.meals.Meal;
import de.mensahub.gateway.JPA.entities.meals.Meals_Menseria_am_Botanischen_Garten;
import de.mensahub.gateway.JPA.entities.mensen.Mensa;
import de.mensahub.gateway.JPA.entities.mensen.Menseria_am_Botanischen_Garten;
import de.mensahub.gateway.JPA.repository.meals.Meals_Menseria_am_Botanischen_GartenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Menseria_am_Botanischen_GartenServices extends Meals_Mensa_Service {

    @Autowired
    Meals_Menseria_am_Botanischen_GartenRepository meals_menseria_am_botanischen_gartenRepository;

    /**
     * Find all meals from Menseria am Botanischen Garten
     * @return Meals Menseria am Botanischen Garten
     */
    @Override
    public Iterable<Meals_Menseria_am_Botanischen_Garten> findAll() {
        return meals_menseria_am_botanischen_gartenRepository.findAll();
    }

    /**
     * Find all meals by serving date greater than or equal to servingDate
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date greater than or equal to serving date from Mensa am Botanischen Garten
     */
    @Override
    public List<Meals_Menseria_am_Botanischen_Garten> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_menseria_am_botanischen_gartenRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * Find all meals by serving date
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by serving date from Mensa am Botanischen Garten
     */
    @Override
    public List<Meals_Menseria_am_Botanischen_Garten> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_menseria_am_botanischen_gartenRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * Save a meal into the database and set the mensa
     * @param meal The meal to be saved
     * @param mensa The mensa the meal is from
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Menseria_am_Botanischen_Garten meals_menseria_am_botanischen_garten = new Meals_Menseria_am_Botanischen_Garten();
        meals_menseria_am_botanischen_garten.setId(meal.getId());
        meals_menseria_am_botanischen_garten.setName(meal.getName());
        meals_menseria_am_botanischen_garten.setCategory(meal.getCategory());
        meals_menseria_am_botanischen_garten.setPrice(meal.getPrice());
        meals_menseria_am_botanischen_garten.setServingDate(meal.getServingDate());
        meals_menseria_am_botanischen_garten.setDescription(meal.getDescription());
        meals_menseria_am_botanischen_garten.setRating(meal.getRating());
        meals_menseria_am_botanischen_garten.setVotes(meal.getVotes());
        meals_menseria_am_botanischen_garten.setStarsTotal(meal.getStarsTotal());
        meals_menseria_am_botanischen_garten.setResponseCode(meal.getResponseCode());

        Menseria_am_Botanischen_Garten menseria_am_botanischen_garten = new Menseria_am_Botanischen_Garten();
        menseria_am_botanischen_garten.setId(mensa.getId());
        menseria_am_botanischen_garten.setName(mensa.getName());
        menseria_am_botanischen_garten.setApiUrl(mensa.getApiUrl());

        meals_menseria_am_botanischen_garten.setMenseria_am_botanischen_garten(menseria_am_botanischen_garten);

        meals_menseria_am_botanischen_gartenRepository.save(meals_menseria_am_botanischen_garten);
    }

    /**
     * Delete a meal from the database
     * @param meal The meal to be deleted
     * @param mensa The mensa the meal is from
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Menseria_am_Botanischen_Garten meals_menseria_am_botanischen_garten = new Meals_Menseria_am_Botanischen_Garten();
        meals_menseria_am_botanischen_garten.setId(meal.getId());
        meals_menseria_am_botanischen_garten.setName(meal.getName());
        meals_menseria_am_botanischen_garten.setCategory(meal.getCategory());
        meals_menseria_am_botanischen_garten.setPrice(meal.getPrice());
        meals_menseria_am_botanischen_garten.setServingDate(meal.getServingDate());
        meals_menseria_am_botanischen_garten.setDescription(meal.getDescription());
        meals_menseria_am_botanischen_garten.setRating(meal.getRating());
        meals_menseria_am_botanischen_garten.setVotes(meal.getVotes());
        meals_menseria_am_botanischen_garten.setStarsTotal(meal.getStarsTotal());
        meals_menseria_am_botanischen_garten.setResponseCode(meal.getResponseCode());

        Menseria_am_Botanischen_Garten menseria_am_botanischen_garten = new Menseria_am_Botanischen_Garten();
        menseria_am_botanischen_garten.setId(mensa.getId());
        menseria_am_botanischen_garten.setName(mensa.getName());
        menseria_am_botanischen_garten.setApiUrl(mensa.getApiUrl());

        meals_menseria_am_botanischen_garten.setMenseria_am_botanischen_garten(menseria_am_botanischen_garten);

        meals_menseria_am_botanischen_gartenRepository.delete(meals_menseria_am_botanischen_garten);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    /**
     * Find all meals by name, serving date and id
     * @param name The name of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @param id The id of the meal
     * @return All meals by name and serving date and id from Mensa am Botanischen Garten
     */
    @Override
    public List<? extends Meal> findByNameAndServingDateAndId(String name, LocalDate servingDate, Long id) {
        return meals_menseria_am_botanischen_gartenRepository.findByNameAndServingDateAndId(name, servingDate, id);
    }

    /**
     * Find all meals by category
     * @param category The category of the meal
     * @return All meals by category from Mensa am Botanischen Garten
     */
    @Override
    public List<? extends Meal> findAllByCategory(String category) {
        return meals_menseria_am_botanischen_gartenRepository.findAllByCategory(category);
    }

    /**
     * Find all meals by category and serving date
     * @param category The category of the meal
     * @param servingDate The date the meal is served (format: YYYY-MM-DD)
     * @return All meals by category and serving date from Mensa am Botanischen Garten
     */
    @Override
    public List<? extends Meal> findAllByCategoryAndServingDate(String category, LocalDate servingDate) {
        return meals_menseria_am_botanischen_gartenRepository.findAllByCategoryAndServingDate(category, servingDate);
    }

    /**
     * Find all meals by rating is less than or equal to the given rating
     * @param rating The rating of the meal as double
     * @return All meals by rating is less than or equal to the given rating from Mensa am Botanischen Garten
     */
    @Override
    public List<? extends Meal> findAllByRatingLessThanEqual(Double rating) {
        return meals_menseria_am_botanischen_gartenRepository.findAllByRatingLessThanEqual(rating);

    }

    /**
     * Find all meals by rating is greater than or equal to the given rating
     * @param rating The rating of the meal as double
     * @return All meals by rating is greater than or equal to the given rating from Mensa am Botanischen Garten
     */
    @Override
    public List<? extends Meal> findAllByRatingGreaterThanEqual(Double rating) {
        return meals_menseria_am_botanischen_gartenRepository.findAllByRatingGreaterThanEqual(rating);
    }

    /**
     * Find all meals by rating is greater than or equal to the given rating and serving date
     * @param startDate startDate (format: YYYY-MM-DD)
     * @param endDate endDate (format: YYYY-MM-DD)
     * @return All meals by rating is greater than or equal to the given rating and serving date from Mensa am Botanischen Garten
     */
    @Override
    public List<? extends Meal> findAllByServingDateGreaterThanEqualAndServingDateLessThanEqual(LocalDate startDate, LocalDate endDate) {
        return meals_menseria_am_botanischen_gartenRepository.findAllByServingDateGreaterThanEqualAndServingDateLessThanEqualOrderByServingDate(startDate, endDate);
    }

}

