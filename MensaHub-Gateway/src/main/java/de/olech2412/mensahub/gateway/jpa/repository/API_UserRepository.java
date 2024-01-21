package de.olech2412.mensahub.gateway.jpa.repository;

import de.olech2412.mensahub.models.authentification.API_User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * API_UserRepositorys are used to create a connection to the database via Spring Data
 */
public interface API_UserRepository extends ListCrudRepository<API_User, Long> {

    Optional<API_User> findAPI_UserByApiUsername(String username);

    Boolean existsAPI_UserByApiUsername(String username);

}