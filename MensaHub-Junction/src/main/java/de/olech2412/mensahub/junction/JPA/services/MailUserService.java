package de.olech2412.mensahub.junction.JPA.services;

import de.olech2412.mensahub.junction.JPA.repository.MailUserRepository;
import de.olech2412.mensahub.junction.JPA.repository.mensen.MensaRepository;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class MailUserService {

    @Autowired
    MailUserRepository mailUserRepository;

    @Autowired
    MensaRepository mensaRepository;

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

    public void deleteMailUser(MailUser mailUser) {
        mailUserRepository.delete(mailUser);
    }
}
