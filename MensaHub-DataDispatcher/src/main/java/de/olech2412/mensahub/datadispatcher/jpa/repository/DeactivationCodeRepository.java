package de.olech2412.mensahub.datadispatcher.jpa.repository;

import de.olech2412.mensahub.models.authentification.DeactivationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeactivationCodeRepository extends CrudRepository<DeactivationCode, Long> {

    List<DeactivationCode> findByCode(String code);
}