package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailUserRepository extends ListCrudRepository<MailUser, Long> {


    List<MailUser> findByEmail(String email);

    MailUser findByActivationCode_Code(String code);

    Optional<MailUser> findByDeactivationCode_Code(String code);

    /**
     * Finds all enabled users
     *
     * @param enabled
     * @return
     */
    List<MailUser> findUsersByEnabled(Boolean enabled);


}