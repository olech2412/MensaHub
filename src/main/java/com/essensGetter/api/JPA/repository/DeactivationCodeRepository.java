package com.essensGetter.api.JPA.repository;

import com.essensGetter.api.JPA.entities.DeactivationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeactivationCodeRepository extends CrudRepository<DeactivationCode, Long> {

    List<DeactivationCode> findByCode(String code);
}