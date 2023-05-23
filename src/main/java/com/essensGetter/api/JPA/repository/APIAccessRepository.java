package com.essensGetter.api.JPA.repository;

import com.essensGetter.api.JPA.entities.APIAccess;
import org.springframework.data.repository.CrudRepository;

public interface APIAccessRepository extends CrudRepository<APIAccess, Long> {

    APIAccess findAPIAccessByToken(String token);

}