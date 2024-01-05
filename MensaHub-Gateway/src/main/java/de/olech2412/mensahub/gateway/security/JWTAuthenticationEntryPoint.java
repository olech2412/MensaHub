package de.olech2412.mensahub.gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint { // This class is used to return a 401 unauthorized error to clients that try to access a protected resource without proper authentication

    /**
     * Sends a 401 unauthorized error to the client
     *
     * @param request       The request
     * @param response      The response
     * @param authException The authentication exception
     * @throws IOException if an error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
