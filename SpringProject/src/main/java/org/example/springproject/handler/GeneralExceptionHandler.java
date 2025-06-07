/**
 * GeneralExceptionHandler.java
 * This class handles exceptions globally for the Spring application.
 */
package org.example.springproject.handler;

import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.EmptyResultException;
import org.example.springproject.exception.ObjectNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GeneralExceptionHandler is a global exception handler that catches various exceptions and returns appropriate HTTP responses.
 * It is annotated with @RestControllerAdvice, which allows it to handle exceptions across all controllers in the application.
 */
@RestControllerAdvice
public class GeneralExceptionHandler {

    /**
     * Handles all exceptions that are not specifically handled by other methods.
     * Returns a 500 Internal Server Error response with the exception message.
     * @param e the exception that was thrown
     * @return a ResponseEntity with status 500 and the exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    /**
     * Handles ObjectNotFound exceptions.
     * Returns a 404 Not Found response with the exception message.
     * @param e the ObjectNotFound exception that was thrown
     * @return a ResponseEntity with status 404 and the exception message
     */
    @ExceptionHandler(ObjectNotFound.class)
    public ResponseEntity<String> handleObjectNotFound(ObjectNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Handles CreationException exceptions.
     * Returns a 400 Bad Request response with the exception message.
     * @param e the CreationException that was thrown
     * @return a ResponseEntity with status 400 and the exception message
     */
    @ExceptionHandler(CreationException.class)
    public ResponseEntity<String> handleCreationException(CreationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Handles EmptyResultException exceptions.
     * Returns a 204 No Content response, indicating that the request was successful but there is no content to return.
     * @param e the EmptyResultException that was thrown
     * @return a ResponseEntity with status 204
     */
    @ExceptionHandler(EmptyResultException.class)
    public ResponseEntity<String> handleEmptyResultException(EmptyResultException e) {
        return ResponseEntity.noContent().build();
    }

}
