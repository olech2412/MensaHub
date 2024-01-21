package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Academica;
import org.springframework.data.repository.ListCrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_AcademicaRepository extends ListCrudRepository<Mensa_Academica, Long> {

    Mensa_Academica findMensa_AcademicaById(Long id);
}