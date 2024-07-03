package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.MensaRepository;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensaService {

    @Autowired
    MensaRepository mensaRepository;

    /**
     * @return Find all mensas
     */
    public List<Mensa> findAll() {
        return mensaRepository.findAll();
    }

    /**
     * @param id The id of the mensa
     * @return Mensa
     */
    public Mensa getMensa(long id) {
        return mensaRepository.findMensaById(id); // There is only one Mensa am Park
    }

    /**
     * @param name The name of the mensa
     * @return Mensa
     */
    public Mensa getMensaByName(String name) {
        return mensaRepository.findMensaByName(name);
    }

}

