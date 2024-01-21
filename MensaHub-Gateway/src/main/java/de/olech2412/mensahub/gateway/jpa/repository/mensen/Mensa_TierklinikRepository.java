package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Tierklinik;
import org.springframework.data.repository.ListCrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_TierklinikRepository extends ListCrudRepository<Mensa_Tierklinik, Long> {

    Mensa_Tierklinik findMensa_TierklinikById(Long id);

}