package de.olech2412.mensahub.gateway.JPA.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Park;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_am_ParkRepository extends CrudRepository<Mensa_am_Park, Long> {

    Mensa_am_Park findMensa_am_ParkById(Long id);

}