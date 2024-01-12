package de.olech2412.mensahub.gateway.controller.authentication;

import de.olech2412.mensahub.gateway.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.gateway.requests.LoginRequest;
import de.olech2412.mensahub.gateway.security.config.JwtService;
import de.olech2412.mensahub.models.authentification.API_User;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Log4j2
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final API_UserRepository apiUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtTokenProvider;

    public AuthenticationController(API_UserRepository apiUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtTokenProvider) {
        this.apiUserRepository = apiUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<API_User> register(@Valid @RequestBody API_User sentApiUser) {
        Optional<API_User> userOptional = apiUserRepository.findAPI_UserByApiUsername(sentApiUser.getApiUsername());
        if (userOptional.isPresent()) {
            log.warn("User tried to register an existing user");
            return ResponseEntity.badRequest().build();
        }

        sentApiUser.setPassword(passwordEncoder.encode(sentApiUser.getPassword()));

        apiUserRepository.save(sentApiUser);

        return ResponseEntity.ok(sentApiUser);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getApiUsername(),
                        loginRequest.getPassword()
                )
        );
        API_User apiUser = null;
        if (apiUserRepository.findAPI_UserByApiUsername(loginRequest.getApiUsername()).isPresent()) {
            apiUser = apiUserRepository.findAPI_UserByApiUsername(loginRequest.getApiUsername()).get();
            apiUser.setLastLogin(LocalDateTime.now());
            apiUserRepository.save(apiUser);
        }

        // Check if user is enabled by admin and verified email
        if (!apiUser.getEnabledByAdmin() || !apiUser.getVerified_email()) {
            return ResponseEntity.badRequest().build(); // if not, return bad request
        }

        log.info("Login successful for user " + loginRequest.getApiUsername());

        return ResponseEntity.ok(jwtTokenProvider.generateToken(
                new User(
                        loginRequest.getApiUsername(),
                        loginRequest.getPassword(),
                        Collections.emptyList()
                )));
    }

}