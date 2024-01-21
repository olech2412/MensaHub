package de.olech2412.mensahub.gateway.jpa.repository.mensen;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Peterssteinweg;
import org.springframework.data.repository.ListCrudRepository;

/**
 * MensaRepositorys are used to create a connection to the database via Spring Data
 */
public interface Mensa_PeterssteinwegRepository extends ListCrudRepository<Mensa_Peterssteinweg, Long> {

    Mensa_Peterssteinweg findMensa_PeterssteinwegById(Long id);

}