package de.olech2412.mensahub.junction.JPA.services.mensen;

import de.olech2412.mensahub.junction.JPA.repository.mensen.Mensa_am_ParkRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Park;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_am_ParkService extends Mensa_Service {

    @Autowired
    Mensa_am_ParkRepository mensa_am_parkRepository;

    /**
     * @return Mensa am Park as List
     */
    @Override
    public List<? extends Mensa> findAll() {
        return mensa_am_parkRepository.findAll();
    }

    /**
     * @return Mensa am Park
     */
    @Override
    public Mensa_am_Park getMensa() {
        List<Mensa_am_Park> mensa_am_parkList = (List<Mensa_am_Park>) mensa_am_parkRepository.findAll();
        return mensa_am_parkList.get(0);
    }
}

