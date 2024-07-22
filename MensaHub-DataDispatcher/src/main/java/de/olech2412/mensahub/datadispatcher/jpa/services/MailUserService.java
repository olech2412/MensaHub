package de.olech2412.mensahub.datadispatcher.jpa.services;

import de.olech2412.mensahub.datadispatcher.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.hibernate.Hibernate;
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
     * Saves a meal to the database
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

    public List<MailUser> findAll() {
        return mailUserRepository.findAll();
    }

    public List<MailUser> findAllByMensasAndEnabled(Mensa mensa, boolean enabled) {
        return mailUserRepository.findAllByMensasAndEnabled(mensa, enabled);
    }

    @Transactional
    public MailUser findByEmail(String email) {
        MailUser mailUser = mailUserRepository.findByEmail(email);
        Hibernate.initialize(mailUser.getMensas());
        return mailUser;
    }
}
