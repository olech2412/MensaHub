package de.mensahub.gateway.JPA.repository;

import de.mensahub.gateway.JPA.entities.authentication.API_User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * API_UserRepositorys are used to create a connection to the database via Spring Data
 */
public interface API_UserRepository extends CrudRepository<API_User, Long> {

    Optional<API_User> findAPI_UserByApiUsername(String username);

}