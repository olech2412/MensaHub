package de.olech2412.mensahub.datadispatcher.Data;

import de.olech2412.mensahub.datadispatcher.JPA.repository.MailUserRepository;
import de.olech2412.mensahub.models.authentification.MailUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Log4j2
@Transactional
@EnableScheduling
public class UserDataDispatcher {

    @Autowired
    private MailUserRepository mailUserRepository;

    public UserDataDispatcher(MailUserRepository mailUserRepository) {
        this.mailUserRepository = mailUserRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkForDeactivatedUsers(){
        Iterable<MailUser> deactivatedMailUsers = mailUserRepository.findUsersByEnabled(false);
        for (MailUser deactivatedMailUser : deactivatedMailUsers) {
            if (deactivatedMailUser.getDeactviatedUntil() != null){
                if (deactivatedMailUser.getDeactviatedUntil().isEqual(LocalDate.now())){
                    log.info("Activating user: " + deactivatedMailUser.getEmail());
                    deactivatedMailUser.setEnabled(true);
                    deactivatedMailUser.setDeactviatedUntil(null);
                    mailUserRepository.save(deactivatedMailUser);
                }
            }
        }
    }

}
