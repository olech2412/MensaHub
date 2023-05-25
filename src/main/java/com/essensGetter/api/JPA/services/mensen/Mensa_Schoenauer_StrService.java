package com.essensGetter.api.JPA.services.mensen;

import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.entities.mensen.Mensa_Peterssteinweg;
import com.essensGetter.api.JPA.entities.mensen.Mensa_Schoenauer_Str;
import com.essensGetter.api.JPA.repository.mensen.Mensa_Schoenauer_StrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_Schoenauer_StrService extends Mensa_Service {

    @Autowired
    Mensa_Schoenauer_StrRepository mensa_schoenauer_strRepository;

    /**
     * @return Mensa Schoenauer Str as Iterable
     */
    @Override
    public Iterable<Mensa_Schoenauer_Str> findAll() {
        return mensa_schoenauer_strRepository.findAll();
    }

    /**
     * @return Mensa Schoenauer Str
     */
    @Override
    public Mensa_Schoenauer_Str getMensa() {
        List<Mensa_Schoenauer_Str> mensa_schoenauer_strList = (List<Mensa_Schoenauer_Str>) mensa_schoenauer_strRepository.findAll();
        return mensa_schoenauer_strList.get(0);
    }
}

