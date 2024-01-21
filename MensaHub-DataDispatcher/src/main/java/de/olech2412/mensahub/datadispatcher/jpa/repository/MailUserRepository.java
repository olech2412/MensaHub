package de.olech2412.mensahub.datadispatcher.jpa.repository;

import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailUserRepository extends ListCrudRepository<MailUser, Long> {


    List<MailUser> findByEmail(String email);

    MailUser findByActivationCode_Code(String code);

    MailUser findByDeactivationCode_Code(String code);

    /**
     * Finds all enabled users
     *
     * @param enabled
     * @return
     */
    List<MailUser> findUsersByEnabled(Boolean enabled);


}