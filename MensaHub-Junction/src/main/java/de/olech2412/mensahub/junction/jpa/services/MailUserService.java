package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.junction.jpa.repository.mensen.MensaRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class MailUserService {

    @Autowired
    MailUserRepository mailUserRepository;

    @Autowired
    MensaRepository mensaRepository;

    @Autowired
    ErrorEntityRepository errorEntityRepository;

    /**
     * Saves a meal for the database
     *
     * @return enabled mail users
     */
    public List<MailUser> findAllUsersThatAreEnabled() {
        return mailUserRepository.findUsersByEnabled(true);
    }

    public List<MailUser> findMailUserByEmail(String email) {
        return mailUserRepository.findByEmail(email);
    }

    @Transactional
    public Result<MailUser, JPAError> findMailUserByDeactivationCode(String deactivationCode) {
        try {
            Optional<MailUser> usersOptional = mailUserRepository.findByDeactivationCode_Code(deactivationCode);
            if (usersOptional.isPresent()) {
                Hibernate.initialize(usersOptional.get().getMensas());
                return Result.success(usersOptional.get());
            } else {
                return Result.error(new JPAError(String.format("MailUser kann nicht durch Code %s gefunden werden", deactivationCode), JPAErrors.ERROR_READ));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<MailUser, JPAError> result = Result.error(new JPAError("Fehler beim lesen der MailUser nach Deaktivierungscode: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    @Transactional
    public void saveMailUser(MailUser mailUser) {
        Set<Mensa> mensas = new HashSet<>();
        for (Mensa mensa : mailUser.getMensas()) {
            Optional<Mensa> mensaFromDB = mensaRepository.findById(mensa.getId());
            if (mensaFromDB.isPresent()) {
                mensas.add(mensaFromDB.get());
            }
        }
        mailUser.setMensas(mensas);

        mailUserRepository.save(mailUser);
    }

    public List<MailUser> findAll() {
        return mailUserRepository.findAll();
    }

    public void deleteMailUser(MailUser mailUser) {
        mailUserRepository.delete(mailUser);
    }
}
