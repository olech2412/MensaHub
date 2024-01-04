package de.olech2412.mensahub.datadispatcher.JPA.services;

import de.olech2412.mensahub.datadispatcher.JPA.repository.MailUserRepository;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MailUserService {

    @Autowired
    MailUserRepository mailUserRepository;

    /**
     * Saves a meal to the database
     *
     * @return enabled mail users
     */
    public Iterable<MailUser> findAllUsersThatAreEnabled() {
        return mailUserRepository.findUsersByEnabled(true);
    }

    public void saveMailUser(MailUser mailUser) {
        mailUserRepository.save(mailUser);
    }

    public void deleteMailUser(MailUser mailUser) {
        mailUserRepository.delete(mailUser);
    }

    public Iterable<MailUser> findAll() {
        return mailUserRepository.findAll();
    }
}
