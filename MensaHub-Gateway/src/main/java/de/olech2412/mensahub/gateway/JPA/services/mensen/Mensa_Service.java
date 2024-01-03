package de.olech2412.mensahub.gateway.JPA.services.mensen;

import de.olech2412.mensahub.gateway.JPA.services.Abstract_Service;
import de.olech2412.mensahub.models.Leipzig.mensen.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public abstract class Mensa_Service extends Abstract_Service<Mensa> { // Class provides following methods for subclasses:

    @Override
    public abstract Iterable<? extends Mensa> findAll();

    public abstract Mensa getMensa();

}
