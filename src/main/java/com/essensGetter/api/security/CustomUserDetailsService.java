package com.essensGetter.api.security;

import com.essensGetter.api.JPA.entities.authentification.Users;
import com.essensGetter.api.JPA.repository.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findById(1L).orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        return new User(
                users.getUsername(),
                users.getPassword(),
                Collections.emptyList()
        );
    }
}
