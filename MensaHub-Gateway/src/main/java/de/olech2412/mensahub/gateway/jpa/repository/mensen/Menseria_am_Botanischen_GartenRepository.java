package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Menseria_am_Botanischen_Garten;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Menseria_am_Botanischen_GartenRepository extends CrudRepository<Menseria_am_Botanischen_Garten, Long> {

    Menseria_am_Botanischen_Garten findMenseria_am_Botanischen_GartenById(Long id);

}