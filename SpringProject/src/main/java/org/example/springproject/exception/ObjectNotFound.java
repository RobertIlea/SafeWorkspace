/**
 * ObjectNotFound.java
 * This class represents a custom exception that is thrown when an object is not found in the system.
 */
package org.example.springproject.exception;

public class ObjectNotFound extends Exception {

    /**
     * Default constructor for ObjectNotFound exception.
     * @param message The detail message for the exception.
     */
    public ObjectNotFound(String message) {
        super(message);
    }

}
