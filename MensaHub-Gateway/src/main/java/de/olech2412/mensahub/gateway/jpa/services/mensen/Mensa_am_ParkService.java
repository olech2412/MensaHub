package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.Mensa_am_ParkRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Park;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mensa_am_ParkService extends Mensa_Service {

    @Autowired
    Mensa_am_ParkRepository mensa_am_parkRepository;

    /**
     * @return Mensa am Park as Iterable
     */
    @Override
    public Iterable<Mensa_am_Park> findAll() {
        return mensa_am_parkRepository.findAll();
    }

    /**
     * @return Mensa am Park
     */
    @Override
    public Mensa_am_Park getMensa() {
        return mensa_am_parkRepository.findMensa_am_ParkById(1L); // There is only one Mensa am Park
    }
}

