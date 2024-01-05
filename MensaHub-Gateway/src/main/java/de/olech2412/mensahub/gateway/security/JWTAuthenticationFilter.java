package de.olech2412.mensahub.gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter { // This class is used to filter the requests and validate the JWT token

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    /**
     * Filters the requests and validates the JWT token
     *
     * @param request     The request
     * @param response    The response
     * @param filterChain The filter chain
     * @throws ServletException If an error occurs
     * @throws IOException      If an error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request); // Get the JWT token from the request

        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) { // If the token is valid, authenticate the user
            String userName = jwtTokenProvider.getUserDataFromToken(jwt); // Get the user data from the token
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName); // Load the user by username
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Create the authentication token

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set the authentication details
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Set the authentication token
        }

        filterChain.doFilter(request, response); // Continue the filter chain
    }

    /**
     * Gets the JWT token from the request
     *
     * @param request The request
     * @return The JWT token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
