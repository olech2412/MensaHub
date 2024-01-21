package de.olech2412.mensahub.gateway.jpa.services;

import de.olech2412.mensahub.models.Mensa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public abstract class Abstract_Service<T> {

    /**
     * Each service has to implement this method to return the entities
     *
     * @return List of all entities
     */
    public abstract List<?> findAll();

}
