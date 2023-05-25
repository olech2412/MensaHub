package com.essensGetter.api.JPA.services.mensen;

import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.entities.mensen.Mensa_Tierklinik;
import com.essensGetter.api.JPA.repository.mensen.Mensa_TierklinikRepository;
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
    public Iterable<Mensa_Tierklinik> findAll() {
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

