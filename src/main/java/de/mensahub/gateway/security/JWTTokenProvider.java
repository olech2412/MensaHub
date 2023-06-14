package de.mensahub.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Log4j2
@Component
public class JWTTokenProvider { // Provides methods to generate, validate and get user data from JWT tokens

    @Value("${api.jwtSecret}") // Value is set in application.properties
    private String jwtSecret;

    /**
     * Generates a JWT token for a user
     *
     * @param userName The user name
     * @return The JWT token
     */
    public String generateToken(String userName) {
        Instant now = Instant.now();
        Instant expiration = now.plus(2, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Gets the user data from a JWT token
     *
     * @param token The JWT token
     * @return The user data
     */
    public String getUserDataFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates a JWT token
     *
     * @param token The JWT token
     * @return True if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException exception) {
            log.error("JWT claims string is empty");
        } catch (ExpiredJwtException exception) {
            log.error("JWT token is expired");
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT token");
        } catch (SignatureException exception) {
            log.error("Invalid JWT signature");
        } catch (UnsupportedJwtException exception) {
            log.error("Unsupported JWT token");
        }

        return false;
    }

    /**
     * Gets the signing key
     * Important because the key is used to sign the JWT token
     *
     * @return
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret); // Decode the secret key from base64
        return Keys.hmacShaKeyFor(keyBytes); // Create the signing key from the decoded secret key bytes and return it as a Key object (used to sign the JWT token)
    }
}
