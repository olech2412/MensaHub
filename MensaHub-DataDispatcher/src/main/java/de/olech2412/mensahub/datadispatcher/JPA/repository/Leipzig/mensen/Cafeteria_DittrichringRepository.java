package de.olech2412.mensahub.datadispatcher.JPA.repository.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Leipzig.mensen.Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Meal;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.CrudRepository;

@Import({Meals_Cafeteria_Dittrichring.class, Meal.class})
public interface Cafeteria_DittrichringRepository extends CrudRepository<Cafeteria_Dittrichring, Long> {
}