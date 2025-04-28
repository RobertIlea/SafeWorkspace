package org.example.springproject.service;

public interface JwtService {
    String generateToken(String email);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);


    String extractEmail(String token);

    String extractId(String token);
}
