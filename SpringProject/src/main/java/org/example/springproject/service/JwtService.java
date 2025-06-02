/**
 * JwtService.java interface for handling JWT operations.
 * This interface defines methods for generating, validating, and extracting information from JWT tokens.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

/**
 * JwtService provides methods to generate a JWT token, extract username and email from the token, validate the token, and extract user ID.
 */
public interface JwtService {

    /**
     * Generates a JWT token for the given email.
     * @param email the email for which to generate the token
     * @return the generated JWT token as a String
     */
    String generateToken(String email);

    /**
     * Validates the given JWT token.
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts the email from the given JWT token.
     * @param token the JWT token from which to extract the email
     * @return the email extracted from the token
     */
    String extractEmail(String token);

}
