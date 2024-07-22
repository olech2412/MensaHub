package de.olech2412.mensahub.junction.jpa.services.mensen;

import de.olech2412.mensahub.junction.jpa.repository.mensen.MensaRepository;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensaService {

    @Autowired
    MensaRepository mensaRepository;

    /**
     * @return mensas List
     */
    public List<Mensa> findAll() {
        return mensaRepository.findAll();
    }

    public Mensa mensaById(Long id) {
        return mensaRepository.findMensaById(id);
    }

    /**
     * @return mensa
     */
    public List<Mensa> getAllMensas() {
        return mensaRepository.findAll();
    }
}

