package de.olech2412.mensahub.junction.JPA.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public abstract class Abstract_Service<T> {

    /**
     * Make sure all subclasses implement this method
     *
     * @return List of all entities
     */
    public abstract List<?> findAll();

}
