package de.olech2412.mensahub.junction.JPA.repository;

import de.olech2412.mensahub.models.authentification.DeactivationCode;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeactivationCodeRepository extends ListCrudRepository<DeactivationCode, Long> {

    List<DeactivationCode> findByCode(String code);
}