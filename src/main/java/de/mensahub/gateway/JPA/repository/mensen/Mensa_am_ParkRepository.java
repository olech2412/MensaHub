package de.mensahub.gateway.JPA.repository.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Park;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_am_ParkRepository extends CrudRepository<Mensa_am_Park, Long> {

    Mensa_am_Park findMensa_am_ParkById(Long id);

}