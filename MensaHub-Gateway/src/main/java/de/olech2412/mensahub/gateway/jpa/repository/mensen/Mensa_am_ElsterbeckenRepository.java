package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Elsterbecken;
import org.springframework.data.repository.ListCrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_am_ElsterbeckenRepository extends ListCrudRepository<Mensa_am_Elsterbecken, Long> {
    Mensa_am_Elsterbecken findMensa_am_ElsterbeckenById(Long id);

}