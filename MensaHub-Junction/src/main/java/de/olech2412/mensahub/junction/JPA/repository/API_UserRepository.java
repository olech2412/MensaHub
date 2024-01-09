package de.olech2412.mensahub.junction.JPA.repository;

import de.olech2412.mensahub.models.authentification.API_User;
import de.olech2412.mensahub.models.authentification.ActivationCode;
import de.olech2412.mensahub.models.authentification.DeactivationCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface API_UserRepository extends CrudRepository<API_User, Long> {

    Optional<API_User> findAPI_UserByApiUsername(String username);

    Optional<API_User> findAPI_UserByActivationCode(ActivationCode code);

    Optional<API_User> findAPI_UserByDeactivationCode(DeactivationCode code);

}