package com.essensGetter.api.JPA.services.mensen;


import com.essensGetter.api.JPA.entities.mensen.Mensa;
import com.essensGetter.api.JPA.services.Abstract_Service;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public abstract class Mensa_Service extends Abstract_Service<Mensa> {

    /**
     * Make sure all subclasses implement this method
     *
     * @return Any subclass of Mensa
     */
    @Override
    public abstract Iterable<? extends Mensa> findAll();

    public abstract Mensa getMensa();

}
