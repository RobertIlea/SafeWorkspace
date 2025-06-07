/**
 * CreationException.java
 * This class defines a custom exception that is thrown when an error occurs during the creation of an object.
 */
package org.example.springproject.exception;

public class CreationException extends Exception{

    /**
     * Default constructor for CreationException.
     * Initializes the exception with a default message.
     * @param message The detail message for the exception.
     */
    public CreationException(String message) {
        super(message);
    }

}
