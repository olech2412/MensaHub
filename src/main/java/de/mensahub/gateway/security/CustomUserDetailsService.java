package de.mensahub.gateway.security;

import de.mensahub.gateway.JPA.entities.authentication.API_User;
import de.mensahub.gateway.JPA.repository.API_UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService { // This class is used to authenticate the user
    private final API_UserRepository apiUserRepository;

    public CustomUserDetailsService(API_UserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    /**
     * Loads the user by username from the database
     *
     * @param username The username of the user
     * @return The user details
     * @throws UsernameNotFoundException If the user was not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        API_User apiUser = apiUserRepository.findAPI_UserByApiUsername(username).orElseThrow(() -> new UsernameNotFoundException("User was not found")); // Find the user by username

        return new User(
                apiUser.getApiUsername(),
                apiUser.getPassword(),
                Collections.emptyList()
        );
    }
}
