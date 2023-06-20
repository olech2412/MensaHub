package de.mensahub.gateway.Controller.authentication;

import de.mensahub.gateway.JPA.entities.authentication.API_User;
import de.mensahub.gateway.JPA.repository.API_UserRepository;
import de.mensahub.gateway.requests.LoginRequest;
import de.mensahub.gateway.security.JWTTokenProvider;
import de.mensahub.gateway.security.MicroMeterConfig;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
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
@Timed
public class AuthenticationController {

    private final API_UserRepository apiUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    private Counter successfulLoginCounter;
    private Counter failedLoginCounter;
    private Counter successfulRegisterCounter;
    private Counter failedRegisterCounter;

    /**
     * Constructor for AuthenticationController
     *
     * @param apiUserRepository     - Repository for API_User
     * @param passwordEncoder       - Password encoder for BCrypt
     * @param authenticationManager - Authentication manager for Spring Security
     * @param jwtTokenProvider      - JWT Token provider for Spring Security
     */
    public AuthenticationController(API_UserRepository apiUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, CompositeMeterRegistry meterRegistry) {
        this.apiUserRepository = apiUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;

        successfulLoginCounter = meterRegistry.counter("successful_login_counter");
        failedLoginCounter = meterRegistry.counter("failed_login_counter");
        successfulRegisterCounter = meterRegistry.counter("successful_register_counter");
        failedRegisterCounter = meterRegistry.counter("failed_register_counter");

    }

    /**
     * Register a new user via POST request
     *
     * @param sentApiUser - User to register
     * @return - Saved user
     */
    @Timed(value = "register")
    @PostMapping("/register")
    public ResponseEntity<API_User> register(@Valid @RequestBody API_User sentApiUser) {
        Optional<API_User> userOptional = apiUserRepository.findAPI_UserByApiUsername(sentApiUser.getApiUsername());
        if (userOptional.isPresent()) {
            log.warn("User tried to register an existing user");
            failedRegisterCounter.increment();
            return ResponseEntity.badRequest().build();
        }

        sentApiUser.setPassword(passwordEncoder.encode(sentApiUser.getPassword())); // Encode password with BCrypt

        apiUserRepository.save(sentApiUser);
        successfulRegisterCounter.increment();
        return ResponseEntity.ok(sentApiUser); // Return saved user

    }

    /**
     * Login a user via POST request
     *
     * @param loginRequest - Request containing username and password
     * @return - JWT Token
     */
    @Timed(value = "login")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @NotNull @NotEmpty LoginRequest loginRequest) { // Post has to contain username and password
        Optional<API_User> userOptional = apiUserRepository.findAPI_UserByApiUsername(loginRequest.getApiUsername()); // Find user by username
        if (userOptional.isEmpty()) {
            log.warn("Username not found: " + loginRequest.getApiUsername());
            failedLoginCounter.increment();
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
                successfulLoginCounter.increment();
                return ResponseEntity.ok(jwtTokenProvider.generateToken(loginRequest.getApiUsername())); // Return JWT Token
            }
        }
        log.warn("User tried to login but is not enabled by admin: " + loginRequest.getApiUsername());
        failedLoginCounter.increment();
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return 401 if user is not enabled by admin
    }

}
