/**
 * PasswordGenerator.java
 * Utility class for generating random passwords.
 * This class provides a method to generate a secure random password
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import java.security.SecureRandom;

/**
 * PasswordGenerator class provides functionality to generate a random password
 */
public class PasswordGenerator {
    /**
     * The characters that can be used in the password.
     * This includes uppercase letters, lowercase letters, digits, and special characters.
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

    /**
     * SecureRandom instance for generating random numbers.
     * This is used to ensure that the generated passwords are secure and unpredictable.
     */
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random password of the specified length.
     *
     * @param length the length of the password to be generated
     * @return a randomly generated password as a String
     */
    public static String generateRandomPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
