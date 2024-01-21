package de.olech2412.mensahub.junction.JPA.services.meals;


import de.olech2412.mensahub.junction.JPA.repository.meals.Meals_Mensa_am_MedizincampusRepository;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class Meals_Mensa_am_MedizincampusService extends Meals_Mensa_Service {

    @Autowired
    Meals_Mensa_am_MedizincampusRepository meals_mensa_am_medizincampusRepository;

    /**
     * @return Meals Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAll() {
        return meals_mensa_am_medizincampusRepository.findAll();
    }

    /**
     * @param servingDate
     * @return All meals by serving date greater than or equal to serving date from Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDateGreaterThanEqual(LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllMealsByServingDateGreaterThanEqual(servingDate);
    }

    /**
     * @param servingDate
     * @return All meals by serving date from Mensa am Medizincampus
     */
    @Override
    public List<Meals_Mensa_am_Medizincampus> findAllMealsByServingDate(LocalDate servingDate) {
        return meals_mensa_am_medizincampusRepository.findAllMealsByServingDate(servingDate);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void save(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Medizincampus meals_mensa_am_medizincampus = new Meals_Mensa_am_Medizincampus();
        meals_mensa_am_medizincampus.setId(meal.getId());
        meals_mensa_am_medizincampus.setName(meal.getName());
        meals_mensa_am_medizincampus.setCategory(meal.getCategory());
        meals_mensa_am_medizincampus.setPrice(meal.getPrice());
        meals_mensa_am_medizincampus.setServingDate(meal.getServingDate());
        meals_mensa_am_medizincampus.setDescription(meal.getDescription());
        meals_mensa_am_medizincampus.setRating(meal.getRating());
        meals_mensa_am_medizincampus.setVotes(meal.getVotes());
        meals_mensa_am_medizincampus.setStarsTotal(meal.getStarsTotal());

        Mensa_am_Medizincampus mensa_am_medizincampus = new Mensa_am_Medizincampus();
        mensa_am_medizincampus.setId(mensa.getId());
        mensa_am_medizincampus.setName(mensa.getName());
        mensa_am_medizincampus.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_medizincampus.setMensa_am_medizincampus(mensa_am_medizincampus);

        meals_mensa_am_medizincampusRepository.save(meals_mensa_am_medizincampus);
    }

    /**
     * @param meal
     * @param mensa
     */
    @Override
    public void delete(Meal meal, Mensa mensa) {
        Meals_Mensa_am_Medizincampus meals_mensa_am_medizincampus = new Meals_Mensa_am_Medizincampus();
        meals_mensa_am_medizincampus.setId(meal.getId());
        meals_mensa_am_medizincampus.setName(meal.getName());
        meals_mensa_am_medizincampus.setCategory(meal.getCategory());
        meals_mensa_am_medizincampus.setPrice(meal.getPrice());
        meals_mensa_am_medizincampus.setServingDate(meal.getServingDate());
        meals_mensa_am_medizincampus.setDescription(meal.getDescription());
        meals_mensa_am_medizincampus.setRating(meal.getRating());
        meals_mensa_am_medizincampus.setVotes(meal.getVotes());
        meals_mensa_am_medizincampus.setStarsTotal(meal.getStarsTotal());

        Mensa_am_Medizincampus mensa_am_medizincampus = new Mensa_am_Medizincampus();
        mensa_am_medizincampus.setId(mensa.getId());
        mensa_am_medizincampus.setName(mensa.getName());
        mensa_am_medizincampus.setApiUrl(mensa.getApiUrl());

        meals_mensa_am_medizincampus.setMensa_am_medizincampus(mensa_am_medizincampus);

        meals_mensa_am_medizincampusRepository.delete(meals_mensa_am_medizincampus);
        log.warn("Meal deleted: " + meal.getName() + " from " + mensa.getName());
    }
}

