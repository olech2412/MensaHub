package de.mensahub.gateway.JPA.repository.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_Schoenauer_Str;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_Schoenauer_StrRepository extends CrudRepository<Mensa_Schoenauer_Str, Long> {

    Mensa_Schoenauer_Str findMensa_Schoenauer_StrById(Long id);

}