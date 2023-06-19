package de.mensahub.gateway.JPA.services.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Cafeteria_Dittrichring;
import de.mensahub.gateway.JPA.repository.mensen.Cafeteria_DittrichringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Cafeteria_DittrichringService extends Mensa_Service {

    @Autowired
    Cafeteria_DittrichringRepository cafeteria_dittrichringRepository;

    /**
     * @return Cafeteria Dittrichring as Iterable
     */
    @Override
    public Iterable<Cafeteria_Dittrichring> findAll() {
        return cafeteria_dittrichringRepository.findAll();
    }

    /**
     * @return Cafeteria Dittrichring
     */
    @Override
    public Cafeteria_Dittrichring getMensa() {
        return cafeteria_dittrichringRepository.findCafeteria_DittrichringById(1L);// There is only one Cafeteria Dittrichring
    }
}
