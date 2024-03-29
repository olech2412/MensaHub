package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.Mensa_Schoenauer_StrRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Schoenauer_Str;
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
    public List<Mensa_Schoenauer_Str> findAll() {
        return mensa_schoenauer_strRepository.findAll();
    }

    /**
     * @return Mensa Schoenauer Str
     */
    @Override
    public Mensa_Schoenauer_Str getMensa() {
        return mensa_schoenauer_strRepository.findMensa_Schoenauer_StrById(1L); // There is only one Mensa Schoenauer Str
    }
}

