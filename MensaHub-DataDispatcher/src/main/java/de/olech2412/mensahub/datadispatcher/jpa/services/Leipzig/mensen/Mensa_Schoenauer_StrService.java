package de.olech2412.mensahub.datadispatcher.jpa.services.Leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.Mensa_Schoenauer_StrRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Schoenauer_Str;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_Schoenauer_StrService extends Mensa_Service {

    @Autowired
    Mensa_Schoenauer_StrRepository mensa_schoenauer_strRepository;

    /**
     * @return Mensa Schoenauer Str as List
     */
    @Override
    public List<? extends Mensa> findAll() {
        return mensa_schoenauer_strRepository.findAll();
    }

    /**
     * @return Mensa Schoenauer Str
     */
    @Override
    public Mensa_Schoenauer_Str getMensa() {
        List<Mensa_Schoenauer_Str> mensa_schoenauer_strList = mensa_schoenauer_strRepository.findAll();
        return mensa_schoenauer_strList.get(0);
    }
}

