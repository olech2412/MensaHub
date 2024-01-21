package de.olech2412.mensahub.datadispatcher.jpa.services.Leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.Mensa_AcademicaRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Academica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mensa_AcademicaService extends Mensa_Service {

    @Autowired
    Mensa_AcademicaRepository mensa_academicaRepository;

    /**
     * @return Mensa Academica as List
     */
    @Override
    public List<Mensa_Academica> findAll() {
        return mensa_academicaRepository.findAll();
    }

    /**
     * @return Mensa Academica
     */
    @Override
    public Mensa_Academica getMensa() {
        List<Mensa_Academica> mensa_academicaList = (List<Mensa_Academica>) mensa_academicaRepository.findAll();
        return mensa_academicaList.get(0);
    }
}
