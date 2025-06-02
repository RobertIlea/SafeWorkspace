/**
 * JwtServiceImpl.java
 * This file is part of the Spring Project.
 * It is used to implement the JwtService interface for handling JWT operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service.implementation;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.springproject.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtServiceImpl is a service class that implements the JwtService interface.
 * It provides methods for generating, validating, and extracting information from JWT tokens.
 */
@Service
public class JwtServiceImpl implements JwtService {

    /**
     * The secret key used for signing the JWT tokens.
     * It is injected from the application properties file.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * The expiration time for the JWT tokens in milliseconds.
     * It is injected from the application properties file.
     */
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * The secret key used for signing the JWT tokens.
     * It is initialized in the init method using the jwtSecret.
     */
    private SecretKey key;

    /**
     * Initializes the secret key using the jwtSecret property.
     * This method is called after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given email.
     * @param email The email for which the token is generated.
     * @return A JWT token as a String.
     */
    @Override
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((new Date(System.currentTimeMillis())).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates the given JWT token.
     * @param token The JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the email from the given JWT token.
     * @param token The JWT token from which to extract the email.
     * @return The email extracted from the token.
     */
    @Override
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
