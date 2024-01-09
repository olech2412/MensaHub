package de.olech2412.mensahub.junction.JPA.services.mensen;

import de.olech2412.mensahub.junction.JPA.repository.mensen.Mensa_TierklinikRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Tierklinik;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_TierklinikService extends Mensa_Service {

    @Autowired
    Mensa_TierklinikRepository mensa_tierklinikRepository;

    /**
     * @return Mensa Tierklinik as Iterable
     */
    @Override
    public Iterable<? extends Mensa> findAll() {
        return mensa_tierklinikRepository.findAll();
    }

    /**
     * @return Mensa Tierklinik
     */
    @Override
    public Mensa_Tierklinik getMensa() {
        List<Mensa_Tierklinik> mensa_tierklinikList = (List<Mensa_Tierklinik>) mensa_tierklinikRepository.findAll();
        return mensa_tierklinikList.get(0);
    }
}

