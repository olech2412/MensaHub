package de.olech2412.mensahub.datadispatcher.jpa.repository;

import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailUserRepository extends ListCrudRepository<MailUser, Long> {


    MailUser findByEmail(String email);


    List<MailUser> findAllByMensasAndEnabled(Mensa mensa, boolean enabled);

    /**
     * Finds all enabled users
     *
     * @param enabled enabled
     * @return all enabled users
     */
    List<MailUser> findUsersByEnabled(Boolean enabled);

    /**
     * Finds all mail users that are enabled and want to receive collaboration info mails
     *
     * @return enabled mail users that want to receive collaboration info mails
     */
    List<MailUser> findMailUsersByWantsCollaborationInfoMailIsTrue();
}