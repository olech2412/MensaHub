package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.Mensa_am_ElsterbeckenRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Elsterbecken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_am_ElsterbeckenService extends Mensa_Service {

    @Autowired
    Mensa_am_ElsterbeckenRepository mensa_am_elsterbeckenRepository;

    /**
     * @return Mensa am Elsterbecken as List
     */
    @Override
    public List<Mensa_am_Elsterbecken> findAll() {
        return mensa_am_elsterbeckenRepository.findAll();
    }

    /**
     * @return Mensa am Elsterbecken
     */
    @Override
    public Mensa_am_Elsterbecken getMensa() {
        return mensa_am_elsterbeckenRepository.findMensa_am_ElsterbeckenById(1L); // There is only one Mensa am Elsterbecken
    }
}

