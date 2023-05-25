package com.essensGetter.api.JPA.services.mensen;

import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.entities.mensen.Mensa_Peterssteinweg;
import com.essensGetter.api.JPA.repository.mensen.Mensa_PeterssteinwegRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_PeterssteinwegService extends Mensa_Service {

    @Autowired
    Mensa_PeterssteinwegRepository mensa_peterssteinwegRepository;

    /**
     * @return Mensa Peterssteinweg as Iterable
     */
    @Override
    public Iterable<Mensa_Peterssteinweg> findAll() {
        return mensa_peterssteinwegRepository.findAll();
    }

    /**
     * @return Mensa Peterssteinweg
     */
    @Override
    public Mensa_Peterssteinweg getMensa() {
        List<Mensa_Peterssteinweg> mensa_peterssteinwegList = (List<Mensa_Peterssteinweg>) mensa_peterssteinwegRepository.findAll();
        return mensa_peterssteinwegList.get(0);
    }
}

