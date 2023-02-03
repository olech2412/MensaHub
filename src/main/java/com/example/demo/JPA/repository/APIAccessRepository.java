package com.example.demo.JPA.repository;

import com.example.demo.JPA.entities.APIAccess;
import org.springframework.data.repository.CrudRepository;

public interface APIAccessRepository extends CrudRepository<APIAccess, Long> {

    APIAccess findAPIAccessByToken(String token);

}