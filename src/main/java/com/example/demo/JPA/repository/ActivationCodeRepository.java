package com.example.demo.JPA.repository;

import com.example.demo.JPA.entities.ActivationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivationCodeRepository extends CrudRepository<ActivationCode, Long> {

    List<ActivationCode> findByCode(String code);

}
