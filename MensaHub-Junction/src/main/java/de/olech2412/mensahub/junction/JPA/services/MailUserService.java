package de.olech2412.mensahub.junction.JPA.services;

import de.olech2412.mensahub.junction.JPA.repository.MailUserRepository;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MailUserService {

    @Autowired
    MailUserRepository mailUserRepository;

    /**
     * Saves a meal for the database
     *
     * @return enabled mail users
     */
    public List<MailUser> findAllUsersThatAreEnabled() {
        return mailUserRepository.findUsersByEnabled(true);
    }

    public void saveMailUser(MailUser mailUser) {
        mailUserRepository.save(mailUser);
    }

    public void deleteMailUser(MailUser mailUser) {
        mailUserRepository.delete(mailUser);
    }
}
