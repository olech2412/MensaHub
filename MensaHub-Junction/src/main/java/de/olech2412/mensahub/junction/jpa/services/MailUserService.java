package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.*;
import de.olech2412.mensahub.junction.jpa.repository.mensen.MensaRepository;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.jobs.Job;
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
    RatingRepository ratingRepository;

    @Autowired
    ErrorEntityRepository errorEntityRepository;

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private DeactivationCodeRepository deactivationCodeRepository;

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
        List<Job> jobs = jobRepository.findAllByMailUsers(List.of(mailUser));
        for (Job job : jobs) {
            List<MailUser> mailUsers = job.getMailUsers();
            mailUsers.removeIf(mailUser1 -> mailUser1.getEmail().equals(mailUser.getEmail()));
            log.info("Removed mailuser {} from job {} because of account deletion", mailUser.getEmail(), job.getUuid());
            if (mailUsers.isEmpty()) {
                job.setMailUsers(null);
            } else {
                job.setMailUsers(mailUsers);
            }
            jobRepository.save(job);
        }

        List<Rating> ratings = ratingRepository.findAllByMailUser(mailUser);
        for (Rating rating : ratings) {
            rating.setMailUser(null);
            ratingRepository.save(rating);
        }
        mailUserRepository.delete(mailUser);
    }

    @Transactional
    public MailUser initialize(MailUser mailUser) {
        List<MailUser> mailUsers = mailUserRepository.findByEmail(mailUser.getEmail());
        mailUser = mailUsers.get(0);
        Hibernate.initialize(mailUser.getSubscriptions());
        Hibernate.initialize(mailUser.getMensas());
        return mailUser;
    }
}
