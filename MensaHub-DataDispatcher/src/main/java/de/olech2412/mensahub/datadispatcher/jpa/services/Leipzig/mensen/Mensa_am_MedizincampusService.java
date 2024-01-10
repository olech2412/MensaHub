package de.olech2412.mensahub.datadispatcher.jpa.services.Leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.Mensa_am_MedizincampusRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_am_MedizincampusService extends Mensa_Service {

    @Autowired
    Mensa_am_MedizincampusRepository mensa_am_medizincampusRepository;

    /**
     * @return Mensa am Medizincampus as Iterable
     */
    @Override
    public Iterable<? extends Mensa> findAll() {
        return mensa_am_medizincampusRepository.findAll();
    }

    /**
     * @return Mensa am Medizincampus
     */
    @Override
    public Mensa_am_Medizincampus getMensa() {
        List<Mensa_am_Medizincampus> mensa_am_medizincampusList = (List<Mensa_am_Medizincampus>) mensa_am_medizincampusRepository.findAll();
        return mensa_am_medizincampusList.get(0);
    }
}
