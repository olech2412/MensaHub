package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.authentification.ActivationCode;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivationCodeRepository extends ListCrudRepository<ActivationCode, Long> {

    List<ActivationCode> findByCode(String code);

}
