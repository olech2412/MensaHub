package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Mensa;
import org.springframework.data.repository.ListCrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface MensaRepository extends ListCrudRepository<Mensa, Long> {

    Mensa findMensaById(Long id);

    Mensa findMensaByName(String name);

}