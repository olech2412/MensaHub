package de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.MensasRepository;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Import({Meal.class, Mensa.class})
public class MensasService {

    @Autowired
    MensasRepository mensasRepository;

    public List<Mensa> findAll() {
        return mensasRepository.findAll();
    }

    public Mensa getMensaByName(String name) {
        List<Mensa> mensas = mensasRepository.findAllByNameEquals(name);
        return mensas.get(0);
    }
}
