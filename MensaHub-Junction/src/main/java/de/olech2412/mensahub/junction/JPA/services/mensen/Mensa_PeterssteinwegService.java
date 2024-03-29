package de.olech2412.mensahub.junction.JPA.services.mensen;

import de.olech2412.mensahub.junction.JPA.repository.mensen.Mensa_PeterssteinwegRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Peterssteinweg;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_PeterssteinwegService extends Mensa_Service {

    @Autowired
    Mensa_PeterssteinwegRepository mensa_peterssteinwegRepository;

    /**
     * @return Mensa Peterssteinweg as List
     */
    @Override
    public List<? extends Mensa> findAll() {
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

