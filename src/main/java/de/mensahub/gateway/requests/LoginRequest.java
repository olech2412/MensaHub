package de.mensahub.gateway.requests;

import lombok.Getter;

@Getter
public class LoginRequest { // This class is used to receive the login data from client

    private String apiUsername;

    private String password;

}
