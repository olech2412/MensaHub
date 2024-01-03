package de.olech2412.mensahub.gateway.JPA.services.mensen;

import de.olech2412.mensahub.gateway.JPA.repository.mensen.Mensa_AcademicaRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Academica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mensa_AcademicaService extends Mensa_Service {

    @Autowired
    Mensa_AcademicaRepository mensa_academicaRepository;

    /**
     * @return Mensa Academica as Iterable
     */
    @Override
    public Iterable<Mensa_Academica> findAll() {
        return mensa_academicaRepository.findAll();
    }

    /**
     * @return Mensa Academica
     */
    @Override
    public Mensa_Academica getMensa() {
        return mensa_academicaRepository.findMensa_AcademicaById(1L); // There is only one Mensa Academica
    }
}
