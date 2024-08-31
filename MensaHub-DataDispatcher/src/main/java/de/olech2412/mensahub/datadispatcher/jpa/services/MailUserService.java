package de.olech2412.mensahub.datadispatcher.jpa.services;

import de.olech2412.mensahub.datadispatcher.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.datadispatcher.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MailUserService {

    @Autowired
    MailUserRepository mailUserRepository;

    @Autowired
    ErrorEntityRepository errorEntityRepository;

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

    public Result<List<MailUser>, JPAError> findMailUsersByWantsCollaborationInfoMailIsTrue() {
        try {
            List<MailUser> mailUsers = mailUserRepository.findMailUsersByWantsCollaborationInfoMailIsTrue();
            return Result.success(mailUsers);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<MailUser>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen der MailUser mit CollabInfoMailOnly: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.DATA_DISPATCHER));
            return result;
        }
    }


    @Transactional
    public MailUser findByEmail(String email) {
        MailUser mailUser = mailUserRepository.findByEmail(email);
        Hibernate.initialize(mailUser.getMensas());
        return mailUser;
    }
}
