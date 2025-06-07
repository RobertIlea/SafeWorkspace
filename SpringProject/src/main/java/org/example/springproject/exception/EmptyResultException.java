/**
 * EmptyResultException.java
 * This class defines a custom exception that is thrown when a result is expected but not found.
 * It is used when not found data is accepted as a valid response.
 */
package org.example.springproject.exception;

public class EmptyResultException extends Exception {

    /**
     * Default constructor for EmptyResultException.
     * Initializes the exception with a default message.
     * @param message The detail message for the exception.
     */
    public EmptyResultException(String message) {
        super(message);
    }
}
