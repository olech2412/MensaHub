package de.olech2412.mensahub.datadispatcher.data;

import de.olech2412.mensahub.datadispatcher.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.models.authentification.MailUser;
import io.micrometer.core.annotation.Counted;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    @Counted(value = "check_for_deactivated_user", description = "Check for deactivated users and activates them")
    public void checkForDeactivatedUsers() {
        List<MailUser> deactivatedMailUsers = mailUserRepository.findUsersByEnabled(false);
        for (MailUser deactivatedMailUser : deactivatedMailUsers) {
            if (deactivatedMailUser.getDeactivatedUntil() != null) {
                if (deactivatedMailUser.getDeactivatedUntil().isEqual(LocalDate.now())) {
                    log.info("Activating user: {}", deactivatedMailUser.getEmail());
                    deactivatedMailUser.setEnabled(true);
                    deactivatedMailUser.setDeactivatedUntil(null);
                    mailUserRepository.save(deactivatedMailUser);
                }
            }
        }
    }

}
