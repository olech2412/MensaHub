package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen;

import de.olech2412.mensahub.models.Meal;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.ListCrudRepository;

@Import({Meal.class, Meal.class})
public interface MealsRepository extends ListCrudRepository<Meal, Long> {
}