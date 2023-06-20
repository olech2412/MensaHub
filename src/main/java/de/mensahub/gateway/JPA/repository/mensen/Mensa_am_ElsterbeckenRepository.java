package de.mensahub.gateway.JPA.repository.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Elsterbecken;
import org.springframework.data.repository.CrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_am_ElsterbeckenRepository extends CrudRepository<Mensa_am_Elsterbecken, Long> {
    Mensa_am_Elsterbecken findMensa_am_ElsterbeckenById(Long id);

}