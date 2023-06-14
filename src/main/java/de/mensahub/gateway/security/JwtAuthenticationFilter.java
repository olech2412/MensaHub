package de.mensahub.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter { // This class is used to filter the requests and validate the JWT token

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
        String jwt = getJwtFroMRequest(request); // Get the JWT token from the request

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
    private String getJwtFroMRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
