package com.example.demo.JPA.services;

import com.example.demo.JPA.entities.MailUser;
import com.example.demo.JPA.repository.MailUserRepository;
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
