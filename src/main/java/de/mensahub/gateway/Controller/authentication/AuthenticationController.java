package de.mensahub.gateway.Controller.authentication;

import de.mensahub.gateway.JPA.entities.authentication.API_User;
import de.mensahub.gateway.JPA.repository.API_UserRepository;
import de.mensahub.gateway.requests.LoginRequest;
import de.mensahub.gateway.security.JWTTokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Log4j2
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final API_UserRepository apiUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    /**
     * Constructor for AuthenticationController
     *
     * @param apiUserRepository     - Repository for API_User
     * @param passwordEncoder       - Password encoder for BCrypt
     * @param authenticationManager - Authentication manager for Spring Security
     * @param jwtTokenProvider      - JWT Token provider for Spring Security
     */
    public AuthenticationController(API_UserRepository apiUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.apiUserRepository = apiUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new user via POST request
     *
     * @param sentApiUser - User to register
     * @return - Saved user
     */
    @PostMapping("/register")
    public ResponseEntity<API_User> register(@Valid @RequestBody API_User sentApiUser) {
        Optional<API_User> userOptional = apiUserRepository.findAPI_UserByApiUsername(sentApiUser.getApiUsername());
        if (userOptional.isPresent()) {
            log.warn("User tried to register an existing user");
            return ResponseEntity.badRequest().build();
        }

        sentApiUser.setPassword(passwordEncoder.encode(sentApiUser.getPassword())); // Encode password with BCrypt

        apiUserRepository.save(sentApiUser);

        return ResponseEntity.ok(sentApiUser); // Return saved user

    }

    /**
     * Login a user via POST request
     *
     * @param loginRequest - Request containing username and password
     * @return - JWT Token
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @NotNull @NotEmpty LoginRequest loginRequest) { // Post has to contain username and password
        Optional<API_User> userOptional = apiUserRepository.findAPI_UserByApiUsername(loginRequest.getApiUsername()); // Find user by username
        if (userOptional.isEmpty()) {
            log.warn("Username not found: " + loginRequest.getApiUsername());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return 401 if user is not found
        } else {
            API_User apiUser = userOptional.get();
            if (apiUser.getEnabledByAdmin().equals(true)) {

                authenticationManager.authenticate( // Authenticate user with Spring Security Authentication Manager
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getApiUsername(),
                                loginRequest.getPassword()
                        )
                );

                apiUser.setLastLogin(LocalDateTime.now()); // Set last login to current time
                apiUserRepository.save(apiUser); // Save changes
                return ResponseEntity.ok(jwtTokenProvider.generateToken(loginRequest.getApiUsername())); // Return JWT Token
            }
        }
        log.warn("User tried to login but is not enabled by admin: " + loginRequest.getApiUsername());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return 401 if user is not enabled by admin
    }

}