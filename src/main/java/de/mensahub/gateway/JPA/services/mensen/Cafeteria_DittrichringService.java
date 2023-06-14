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
        List<Cafeteria_Dittrichring> cafeteria_dittrichringList = (List<Cafeteria_Dittrichring>) cafeteria_dittrichringRepository.findAll();
        return cafeteria_dittrichringList.get(0); // There is only one Cafeteria Dittrichring
    }
}
