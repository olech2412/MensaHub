package de.mensahub.gateway.JPA.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public abstract class Abstract_Service<T> {

    /**
     * Each service has to implement this method to return the entities
     * @return Iterable of all entities
     */
    public abstract Iterable findAll();

}
