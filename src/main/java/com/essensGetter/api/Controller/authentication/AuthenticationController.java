package com.essensGetter.api.Controller.authentication;

import com.essensGetter.api.JPA.entities.authentication.API_User;
import com.essensGetter.api.JPA.repository.API_UserRepository;
import com.essensGetter.api.requests.LoginRequest;
import com.essensGetter.api.security.JWTTokenProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Log4j2
public class AuthenticationController {

    private final API_UserRepository apiUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenProvider jwtTokenProvider;

    public AuthenticationController(API_UserRepository apiUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
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

        sentApiUser.setCreationDate(LocalDate.now());
        sentApiUser.setPassword(passwordEncoder.encode(sentApiUser.getPassword()));
        sentApiUser.setNumberOfRequests(0L);

        apiUserRepository.save(sentApiUser);

        return ResponseEntity.ok(sentApiUser);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getApiUsername(),
                        loginRequest.getPassword()
                )
        );

        return ResponseEntity.ok(jwtTokenProvider.generateToken(loginRequest.getApiUsername()));
    }

}
