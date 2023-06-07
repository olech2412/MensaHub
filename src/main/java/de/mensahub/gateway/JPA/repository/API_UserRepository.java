package de.mensahub.gateway.JPA.repository;

import de.mensahub.gateway.JPA.entities.authentication.API_User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface API_UserRepository extends CrudRepository<API_User, Long> {

    Optional<API_User> findAPI_UserByApiUsername(String username);

}