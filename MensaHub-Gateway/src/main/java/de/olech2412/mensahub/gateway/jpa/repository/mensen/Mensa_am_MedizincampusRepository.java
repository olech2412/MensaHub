package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Medizincampus;
import org.springframework.data.repository.ListCrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_am_MedizincampusRepository extends ListCrudRepository<Mensa_am_Medizincampus, Long> {

    Mensa_am_Medizincampus findMensa_am_MedizincampusById(Long id);

}