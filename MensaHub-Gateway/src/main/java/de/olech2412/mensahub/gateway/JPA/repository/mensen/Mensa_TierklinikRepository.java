package de.olech2412.mensahub.gateway.JPA.repository.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_Tierklinik;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_TierklinikRepository extends CrudRepository<Mensa_Tierklinik, Long> {

    Mensa_Tierklinik findMensa_TierklinikById(Long id);

}