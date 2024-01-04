package de.olech2412.mensahub.datadispatcher.JPA.repository;

import de.olech2412.mensahub.models.authentification.ActivationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivationCodeRepository extends CrudRepository<ActivationCode, Long> {

    List<ActivationCode> findByCode(String code);

}
