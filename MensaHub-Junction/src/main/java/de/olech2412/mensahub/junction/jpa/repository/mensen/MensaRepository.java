package de.olech2412.mensahub.junction.jpa.repository.mensen;


import de.olech2412.mensahub.models.Mensa;
import org.springframework.data.repository.ListCrudRepository;

public interface MensaRepository extends ListCrudRepository<Mensa, Long> {

    Mensa findMensaById(Long id);
}