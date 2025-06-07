/**
 * User.java
 * This class represents a User collection in the Firestore.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.entity;

/**
 * Represents a user in the system.
 */
public class User {

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The phone number of the user.
     */
    private String phone;

    /**
     * Default constructor for User.
     * Required for Firestore deserialization.
     */
    public User() {}

    /**
     * Constructs a User with the specified details.
     * @param name the name of the user
     * @param email the email of the user
     * @param password the password of the user
     * @param phone the phone number of the user
     */
    public User(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    /**
     * Constructs a User with the specified name, email, and password.
     * @param name the name of the user
     * @param email the email of the user
     * @param password the password of the user
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Returns the name of the user.
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the email of the user.
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password of the user.
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the phone number of the user.
     * @return the phone number of the user
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
