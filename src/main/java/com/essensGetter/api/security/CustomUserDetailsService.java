package com.essensGetter.api.security;

import com.essensGetter.api.JPA.entities.authentication.API_User;
import com.essensGetter.api.JPA.repository.API_UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final API_UserRepository apiUserRepository;

    public CustomUserDetailsService(API_UserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        API_User apiUser = apiUserRepository.findAPI_UserByApiUsername(username).orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        return new User(
                apiUser.getApiUsername(),
                apiUser.getPassword(),
                Collections.emptyList()
        );
    }
}
