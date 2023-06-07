package de.mensahub.gateway.JPA.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public abstract class Abstract_Service<T> {

    /**
     * Make sure all subclasses implement this method
     *
     * @return Iterable of all entities
     */
    public abstract Iterable findAll();

}
