package com.epam.finaltask.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private long jwtExpiration;
    private SecretKey signingKey;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String rawKey,
            @Value("${application.security.jwt.expiration}") long jwtExpiration) {

        this.jwtExpiration = jwtExpiration;

        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(rawKey);
        this.signingKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

        log.info("JwtService fully initialized with signing key hash: {}", this.signingKey.hashCode());
    }

    /**
     * Generate a brand-new token using just the username.
     */
    public String generateToken(String username) {
        System.out.println("KEY USED TO GENERATE: " + this.signingKey.hashCode());
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Extract the username (Subject) from the JWT token string.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Check if the token belongs to the right user and isn't expired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        System.out.println("DEBUG -> username is: " + username);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private String createToken(Map<String, Object> claims, String subject) {
        String token =  Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(signingKey)
                .compact();
        System.out.println("GENERATE TOKEN -> " + token);
        return token;
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseToken(String token) {
        System.out.println("PARSING TOKEN  -> " + token);
        System.out.println("KEY USED TO PARSE: " + this.signingKey.hashCode());
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
