/**
 * UserService.java
 * This interface defines the contract for user-related operations in the application.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.service;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;

import java.util.List;

/**
 * UserService interface provides methods for managing users in the application.
 * It includes operations for adding, deleting, updating, and retrieving user information.
 */
public interface UserService {

    /**
     * Adds a new user to the system.
     * @param user The user to be added.
     * @return UserDTO containing the details of the added user.
     * @throws RuntimeException if an error occurs during the addition of the user.
     */
    UserDTO addUser(User user);

    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to be deleted.
     * @return UserDTO containing the details of the deleted user.
     * @throws RuntimeException if an error occurs during the deletion of the user.
     */
    UserDTO deleteUserById(String id);

    /**
     * Updates an existing user.
     * @param id The ID of the user to be updated.
     * @param updatedUser The updated user information.
     * @return UserDTO containing the details of the updated user.
     */
    UserDTO updateUser(String id, User updatedUser);

    /**
     * Retrieves a list of all users.
     * @return List of UserDTO containing details of all users.
     */
    List<UserDTO> getUsers();

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user to be retrieved.
     * @return UserDTO containing the details of the user.
     */
    UserDTO getUserById(String id);

    /**
     * Retrieves a user by their email address.
     * @param email The email address of the user to be retrieved.
     * @return User containing the details of the user.
     */
    User getUserByEmail(String email);

    /**
     * Retrieves a user ID by their email address.
     * @param email The email address of the user.
     * @return String containing the user ID.
     */
    String getUserIdByEmail(String email);

    /**
     * Retrieves a user by their room ID.
     * @param roomId The room ID associated with the user.
     * @return UserDTO containing the details of the user.
     */
    UserDTO getUserByRoomId(String roomId);

    /**
     * Updates the phone number of a user.
     * @param userId The ID of the user whose phone number is to be updated.
     * @param phone The new phone number to be set for the user.
     */
    void updateUserPhone(String userId, String phone);

    /**
     * Retrieves the phone number of a user by their user ID.
     * @param userId The ID of the user whose phone number is to be retrieved.
     * @return String containing the user's phone number.
     */
    String getUserPhoneNumber(String userId);
}
