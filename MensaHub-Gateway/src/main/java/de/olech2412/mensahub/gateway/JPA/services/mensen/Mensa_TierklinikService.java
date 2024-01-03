package de.olech2412.mensahub.gateway.JPA.services.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_Tierklinik;
import de.mensahub.gateway.JPA.repository.mensen.Mensa_TierklinikRepository;
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

