package de.olech2412.mensahub.gateway.JPA.repository;

import de.mensahub.gateway.JPA.entities.authentication.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * UsersRepository are used to create a connection to the database via Spring Data
 */
public interface UsersRepository extends CrudRepository<Users, Long> {

    /**
     * Finds all enabled users
     *
     * @param enabled
     * @return
     */
    Iterable<Users> findUsersByEnabled(Boolean enabled);
}
