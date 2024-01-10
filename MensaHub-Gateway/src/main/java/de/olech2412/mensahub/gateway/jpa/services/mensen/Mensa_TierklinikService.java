package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.Mensa_TierklinikRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Tierklinik;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mensa_TierklinikService extends Mensa_Service {

    @Autowired
    Mensa_TierklinikRepository mensa_tierklinikRepository;

    /**
     * @return Mensa Tierklinik as Iterable
     */
    @Override
    public Iterable<Mensa_Tierklinik> findAll() {
        return mensa_tierklinikRepository.findAll();
    }

    /**
     * @return Mensa Tierklinik
     */
    @Override
    public Mensa_Tierklinik getMensa() {
        return mensa_tierklinikRepository.findMensa_TierklinikById(1L); // There is only one Mensa Tierklinik
    }
}

