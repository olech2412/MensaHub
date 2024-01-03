package de.olech2412.mensahub.gateway.JPA.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Cafeteria_Dittrichring;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Cafeteria_DittrichringRepository extends CrudRepository<Cafeteria_Dittrichring, Long> {

    Cafeteria_Dittrichring findCafeteria_DittrichringById(Long id);

}