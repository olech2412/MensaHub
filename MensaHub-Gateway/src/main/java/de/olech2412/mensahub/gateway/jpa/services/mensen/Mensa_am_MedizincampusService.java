package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.Mensa_am_MedizincampusRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Medizincampus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mensa_am_MedizincampusService extends Mensa_Service {

    @Autowired
    Mensa_am_MedizincampusRepository mensa_am_medizincampusRepository;

    /**
     * @return Mensa am Medizincampus as Iterable
     */
    @Override
    public Iterable<Mensa_am_Medizincampus> findAll() {
        return mensa_am_medizincampusRepository.findAll();
    }

    /**
     * @return Mensa am Medizincampus
     */
    @Override
    public Mensa_am_Medizincampus getMensa() {
        return mensa_am_medizincampusRepository.findMensa_am_MedizincampusById(1L); // There is only one Mensa am Medizincampus
    }
}
