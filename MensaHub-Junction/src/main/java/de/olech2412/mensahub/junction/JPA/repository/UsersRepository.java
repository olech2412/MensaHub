package de.olech2412.mensahub.junction.JPA.repository;

import de.olech2412.mensahub.models.authentification.Users;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends ListCrudRepository<Users, Long> {

    /**
     * Finds all enabled users
     *
     * @param enabled
     * @return
     */
    List<Users> findUsersByEnabled(Boolean enabled);
}
