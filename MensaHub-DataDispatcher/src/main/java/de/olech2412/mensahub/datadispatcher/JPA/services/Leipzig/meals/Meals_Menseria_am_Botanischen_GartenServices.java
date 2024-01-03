package de.olech2412.mensahub.datadispatcher.JPA.services.Leipzig.meals;

import de.olech2412.mensahub.datadispatcher.JPA.repository.Leipzig.meals.Meals_Menseria_am_Botanischen_GartenRepository;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Menseria_am_Botanischen_Garten;
import de.olech2412.mensahub.models.Leipzig.mensen.Menseria_am_Botanischen_Garten;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
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
     * @return Meals Menseria am Botanischen Garten
     */
    @Override
    public Iterable<Meals_Menseria_am_Botanischen_Garten> findAll() {
        return meals_menseria_am_botanischen_gartenRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Menseria am Botanischen Garten
     */
    @Override
    public List<Meals_Menseria_am_Botanischen_Garten> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_menseria_am_botanischen_gartenRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Menseria am Botanischen Garten
     */
    @Override
    public List<Meals_Menseria_am_Botanischen_Garten> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_menseria_am_botanischen_gartenRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
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
        meals_menseria_am_botanischen_garten.setAdditionalInfo(meal.getAdditionalInfo());
        meals_menseria_am_botanischen_garten.setAllergens(meal.getAllergens());
        meals_menseria_am_botanischen_garten.setAdditives(meal.getAdditives());
        meals_menseria_am_botanischen_garten.setRating(meal.getRating());
        meals_menseria_am_botanischen_garten.setVotes(meal.getVotes());
        meals_menseria_am_botanischen_garten.setStarsTotal(meal.getStarsTotal());

        Menseria_am_Botanischen_Garten menseria_am_botanischen_garten = new Menseria_am_Botanischen_Garten();
        menseria_am_botanischen_garten.setId(mensa.getId());
        menseria_am_botanischen_garten.setName(mensa.getName());
        menseria_am_botanischen_garten.setApiUrl(mensa.getApiUrl());

        meals_menseria_am_botanischen_garten.setMenseria_am_botanischen_garten(menseria_am_botanischen_garten);

        meals_menseria_am_botanischen_gartenRepository.save(meals_menseria_am_botanischen_garten);
    }

    @Override
    public void saveAll(List<? extends Meal> meals, Mensa mensa) {
        for (Meal meal : meals) {
            Meals_Menseria_am_Botanischen_Garten meals_menseria_am_botanischen_garten = new Meals_Menseria_am_Botanischen_Garten();
            meals_menseria_am_botanischen_garten.setId(meal.getId());
            meals_menseria_am_botanischen_garten.setName(meal.getName());
            meals_menseria_am_botanischen_garten.setCategory(meal.getCategory());
            meals_menseria_am_botanischen_garten.setPrice(meal.getPrice());
            meals_menseria_am_botanischen_garten.setServingDate(meal.getServingDate());
            meals_menseria_am_botanischen_garten.setDescription(meal.getDescription());
            meals_menseria_am_botanischen_garten.setAdditionalInfo(meal.getAdditionalInfo());
            meals_menseria_am_botanischen_garten.setAllergens(meal.getAllergens());
            meals_menseria_am_botanischen_garten.setAdditives(meal.getAdditives());
            meals_menseria_am_botanischen_garten.setRating(meal.getRating());
            meals_menseria_am_botanischen_garten.setVotes(meal.getVotes());
            meals_menseria_am_botanischen_garten.setStarsTotal(meal.getStarsTotal());

            Menseria_am_Botanischen_Garten menseria_am_botanischen_garten = new Menseria_am_Botanischen_Garten();
            menseria_am_botanischen_garten.setId(mensa.getId());
            menseria_am_botanischen_garten.setName(mensa.getName());
            menseria_am_botanischen_garten.setApiUrl(mensa.getApiUrl());

            meals_menseria_am_botanischen_garten.setMenseria_am_botanischen_garten(menseria_am_botanischen_garten);

            meals_menseria_am_botanischen_gartenRepository.save(meals_menseria_am_botanischen_garten);
        }
    }

    @Override
    public void deleteAllByServingDate(LocalDate servingDate) {
        meals_menseria_am_botanischen_gartenRepository.deleteAllByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
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
        meals_menseria_am_botanischen_garten.setAdditionalInfo(meal.getAdditionalInfo());
        meals_menseria_am_botanischen_garten.setAllergens(meal.getAllergens());
        meals_menseria_am_botanischen_garten.setAdditives(meal.getAdditives());
        meals_menseria_am_botanischen_garten.setRating(meal.getRating());
        meals_menseria_am_botanischen_garten.setVotes(meal.getVotes());
        meals_menseria_am_botanischen_garten.setStarsTotal(meal.getStarsTotal());

        Menseria_am_Botanischen_Garten menseria_am_botanischen_garten = new Menseria_am_Botanischen_Garten();
        menseria_am_botanischen_garten.setId(mensa.getId());
        menseria_am_botanischen_garten.setName(mensa.getName());
        menseria_am_botanischen_garten.setApiUrl(mensa.getApiUrl());

        meals_menseria_am_botanischen_garten.setMenseria_am_botanischen_garten(menseria_am_botanischen_garten);

        meals_menseria_am_botanischen_gartenRepository.delete(meals_menseria_am_botanischen_garten);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }

    @Override
    public List<? extends Meal> findMealsFromMensaByNameAndServingDateBeforeOrderByServingDateDesc(String name, LocalDate servingDate) {
        return meals_menseria_am_botanischen_gartenRepository.findMeals_Menseria_am_Botanischen_GartenByNameAndServingDateBeforeOrderByServingDateDesc(name, servingDate);
    }

}

