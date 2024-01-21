package de.olech2412.mensahub.datadispatcher.jpa.services.Leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.Cafeteria_DittrichringRepository;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Leipzig.mensen.Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Meal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Import({Meals_Cafeteria_Dittrichring.class, Meal.class})
public class Cafeteria_DittrichringService extends Mensa_Service {

    @Autowired
    Cafeteria_DittrichringRepository cafeteria_dittrichringRepository;

    /**
     * @return Cafeteria Dittrichring as List
     */
    @Override
    public List<Cafeteria_Dittrichring> findAll() {
        return cafeteria_dittrichringRepository.findAll();
    }

    /**
     * @return Cafeteria Dittrichring
     */
    @Override
    public Cafeteria_Dittrichring getMensa() {
        List<Cafeteria_Dittrichring> cafeteria_dittrichringList = (List<Cafeteria_Dittrichring>) cafeteria_dittrichringRepository.findAll();
        return cafeteria_dittrichringList.get(0);
    }
}
