package com.essensGetter.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Log4j2
@Component
public class JWTTokenProvider {

    @Value("${api.jwtSecret}")
    private String jwtSecret;

    public String generateToken(String userEmail) {
        Instant now = Instant.now();
        Instant expiration = now.plus(7, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserMailFromToken(String token){
        Claims claims = Jwts.parserBuilder().build()
                .setSigningKey(jwtSecret)
                .parseClaimsJwt(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().build().setSigningKey(jwtSecret).parseClaimsJws(token);
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
}
