package de.olech2412.mensahub.datadispatcher.jpa.services.Leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.services.Abstract_Service;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public abstract class Mensa_Service extends Abstract_Service<Mensa> {

    /**
     * Make sure all subclasses implement this method
     *
     * @return Any subclass of Mensa
     */
    @Override
    public abstract List<? extends Mensa> findAll();

    public abstract Mensa getMensa();

}
