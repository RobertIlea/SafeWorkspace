/**
 * UserController.java
 * This file represents the REST controller for managing user-related operations.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.ObjectNotFound;
import org.example.springproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserController handles HTTP requests related to user operations.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/user" URL path and allows cross-origin requests from "<a href="http://localhost:4200">localhost:4200</a>".
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    /**
     * The UserService is injected to handle business logic related to users.
     */
    @Autowired
    private UserService userService;

    /**
     * This method handles GET requests to retrieve all users.
     * It returns a list of UserDTO objects.
     * If the user list is empty or not found, it throws an ObjectNotFound exception.
     * @return ResponseEntity containing a list of UserDTO objects
     * @throws ObjectNotFound if the user list is empty or not found
     */
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsers() throws ObjectNotFound {
        List<UserDTO> users = userService.getUsers();

        if(users == null || users.isEmpty()) {
            throw new ObjectNotFound("User list is empty or not found");
        }

        return ResponseEntity.ok(users);
    }

    /**
     * This method handles GET requests to retrieve a user by their ID.
     * @param id the ID of the user to retrieve
     * @return ResponseEntity containing the UserDTO object
     * @throws ObjectNotFound if the user with the specified ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) throws ObjectNotFound {
        UserDTO userDTO = userService.getUserById(id);

        if(userDTO == null) {
            throw new ObjectNotFound("User with ID " + id + " not found");
        }

        return ResponseEntity.ok(userDTO);
    }

    /**
     * This method handles POST requests to add a new user.
     * @param user the User object to be added
     * @return ResponseEntity containing the created UserDTO object
     * @throws CreationException if the user could not be added
     */
    @PostMapping("/")
    public ResponseEntity<UserDTO> addUser(@RequestBody User user) throws CreationException {
        UserDTO userDTO = userService.addUser(user);

        if(userDTO == null) {
                throw new CreationException("Failed to add user");
        }

        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    /**
     * This method handles DELETE requests to remove a user by their ID.
     * @param id the ID of the user to be deleted
     * @return ResponseEntity containing the deleted UserDTO object
     * @throws ObjectNotFound if the user with the specified ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable String id) throws ObjectNotFound {
        UserDTO userDTO = userService.deleteUserById(id);

        if(userDTO == null) {
            throw new ObjectNotFound("User with ID " + id + " not found");
        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * This method handles PUT requests to update a user by their ID.
     * @param id the ID of the user to be updated
     * @param user the User object containing updated information
     * @return ResponseEntity containing the updated UserDTO object
     * @throws ObjectNotFound if the user with the specified ID is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody User user) throws ObjectNotFound {
        UserDTO userDTO = userService.updateUser(id,user);

        if(userDTO == null) {
            throw new ObjectNotFound("User with ID " + id + " not found");
        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * This method handles PUT requests to update a user's phone number by their ID.
     * @param id the ID of the user whose phone number is to be updated
     * @param requestBody a map containing the new phone number
     * @return ResponseEntity indicating success or failure
     * @throws ObjectNotFound if the user with the specified ID is not found or if the phone number is invalid
     */
    @PutMapping("{id}/phone")
    public ResponseEntity<String> updateUserPhone(@PathVariable String id, @RequestBody Map<String, String> requestBody ) throws ObjectNotFound {
        String phoneNumber = requestBody.get("phoneNumber");

        if(phoneNumber == null || phoneNumber.isEmpty()) {
            throw new ObjectNotFound("Phone number cannot be null or empty");
        }

        userService.updateUserPhone(id,phoneNumber);

        return new ResponseEntity<>("Phone number updated!",HttpStatus.OK);

    }

    /**
     * This method handles GET requests to retrieve a user's ID by their email.
     * @param email the email of the user whose ID is to be retrieved
     * @return ResponseEntity containing the user's ID
     * @throws ObjectNotFound if the user with the specified email is not found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<String> getUserIdByEmail(@PathVariable String email) throws ObjectNotFound {
        String userId = userService.getUserIdByEmail(email);

        if(userId == null) {
            throw new ObjectNotFound("User with email " + email + " not found");
        }

        return new ResponseEntity<>(userId, HttpStatus.OK);

    }
}
