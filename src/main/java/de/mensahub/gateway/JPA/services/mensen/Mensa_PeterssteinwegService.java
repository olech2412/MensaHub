package de.mensahub.gateway.JPA.services.mensen;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_Peterssteinweg;
import de.mensahub.gateway.JPA.repository.mensen.Mensa_PeterssteinwegRepository;
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
        return mensa_peterssteinwegList.get(0); // There is only one Mensa Peterssteinweg
    }
}

