package com.essensGetter.api.security;

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
public class JWTTokenProvider {

    @Value("${api.jwtSecret}")
    private String jwtSecret;

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

    public String getUserDataFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

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

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
