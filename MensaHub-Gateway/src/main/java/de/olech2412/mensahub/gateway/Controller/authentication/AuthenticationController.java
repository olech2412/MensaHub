package de.olech2412.mensahub.gateway.Controller.authentication;

import de.olech2412.mensahub.gateway.JPA.repository.API_UserRepository;
import de.olech2412.mensahub.gateway.requests.LoginRequest;
import de.olech2412.mensahub.gateway.security.JWTTokenProvider;
import de.olech2412.mensahub.models.authentification.API_User;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private final Counter successfulLoginCounter;
    private final Counter failedLoginCounter;
    private final Counter successfulRegisterCounter;
    private final Counter failedRegisterCounter;

    public AuthenticationController(API_UserRepository apiUserRepository,
                                    PasswordEncoder passwordEncoder,
                                    AuthenticationManager authenticationManager,
                                    JWTTokenProvider jwtTokenProvider,
                                    CompositeMeterRegistry meterRegistry) {
        this.apiUserRepository = apiUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;

        successfulLoginCounter = meterRegistry.counter("successful_login_counter");
        failedLoginCounter = meterRegistry.counter("failed_login_counter");
        successfulRegisterCounter = meterRegistry.counter("successful_register_counter");
        failedRegisterCounter = meterRegistry.counter("failed_register_counter");
    }

    @Timed(value = "register")
    @PostMapping("/register")
    public ResponseEntity<API_User> registerUser(@Valid @RequestBody API_User sentApiUser) {
        if (apiUserRepository.existsAPI_UserByApiUsername(sentApiUser.getApiUsername())) {
            log.warn("User tried to register an existing user");
            failedRegisterCounter.increment();
            return ResponseEntity.badRequest().build();
        }

        sentApiUser.setPassword(passwordEncoder.encode(sentApiUser.getPassword()));
        apiUserRepository.save(sentApiUser);
        successfulRegisterCounter.increment();
        return ResponseEntity.ok(sentApiUser);
    }

    @Timed(value = "login")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @NotNull @NotEmpty LoginRequest loginRequest) {
        Optional<API_User> userOptional = apiUserRepository.findAPI_UserByApiUsername(loginRequest.getApiUsername());
        if (userOptional.isEmpty()) {
            log.warn("Username not found: " + loginRequest.getApiUsername());
            failedLoginCounter.increment();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        API_User apiUser = userOptional.get();
        if (!apiUser.getEnabledByAdmin()) {
            log.warn("User tried to login but is not enabled by admin: " + loginRequest.getApiUsername());
            failedLoginCounter.increment();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getApiUsername(),
                        loginRequest.getPassword()
                )
        );

        apiUser.setLastLogin(LocalDateTime.now());
        apiUserRepository.save(apiUser);
        successfulLoginCounter.increment();
        return ResponseEntity.ok(jwtTokenProvider.generateToken(loginRequest.getApiUsername()));
    }
}