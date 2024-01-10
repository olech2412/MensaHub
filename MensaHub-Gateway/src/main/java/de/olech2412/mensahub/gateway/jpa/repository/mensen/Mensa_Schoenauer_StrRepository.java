package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Schoenauer_Str;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_Schoenauer_StrRepository extends CrudRepository<Mensa_Schoenauer_Str, Long> {

    Mensa_Schoenauer_Str findMensa_Schoenauer_StrById(Long id);

}