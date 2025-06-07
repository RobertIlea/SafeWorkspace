/**
 * UserDTO.java
 * This class represents a Data Transfer Object (DTO) for user data.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.dto;

/**
 * UserDTO is used to transfer user data between different layers of the application.
 */
public class UserDTO {

    /**
     * Unique identifier for the user.
     */
    private String id;

    /**
     * Name of the user.
     */
    private String name;

    /**
     * Email address of the user.
     */
    private String email;

    private String password;

    /**
     * Default constructor for UserDTO.
     */
    public UserDTO() {}

    /**
     * Parameterized constructor for UserDTO.
     * @param id Unique identifier for the user.
     * @param name Name of the user.
     * @param email Email address of the user.
     */
    public UserDTO(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Gets the unique identifier of the user.
     * @return Unique identifier of the user.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     * @param id Unique identifier to set for the user.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the user.
     * @return Name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     * @param name Name to set for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the user.
     * @return Email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * @param email Email address to set for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the UserDTO object.
     * @return String representation of the UserDTO.
     */
    @Override
    public String toString() {
        return String.format("{\"name\":\"%s\",\"email\":\"%s\"}", name, email);
    }
}
