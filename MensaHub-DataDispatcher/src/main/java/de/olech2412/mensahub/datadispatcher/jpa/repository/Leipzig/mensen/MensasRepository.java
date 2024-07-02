package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen;

import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

@Import({Mensa.class, Meal.class})
public interface MensasRepository extends ListCrudRepository<Mensa, Long> {

    List<Mensa> findAllByNameEquals(String name);

}