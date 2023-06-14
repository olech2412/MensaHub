package de.mensahub.gateway.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint { // This class is used to return a 401 unauthorized error to clients that try to access a protected resource without proper authentication

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
