package de.olech2412.mensahub.gateway.JPA.services.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Elsterbecken;
import de.mensahub.gateway.JPA.repository.mensen.Mensa_am_ElsterbeckenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mensa_am_ElsterbeckenService extends Mensa_Service {

    @Autowired
    Mensa_am_ElsterbeckenRepository mensa_am_elsterbeckenRepository;

    /**
     * @return Mensa am Elsterbecken as Iterable
     */
    @Override
    public Iterable<Mensa_am_Elsterbecken> findAll() {
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

