/**
 * AuthController.java
 * This controller handles user authentication and login functionality.
 * It provides endpoints for user login and returns a JWT token upon successful authentication.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.controller;

import org.example.springproject.dto.RegisterDTO;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.exception.CreationException;
import org.example.springproject.exception.ObjectNotFound;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthController handles HTTP requests related to user authentication.
 * It is marked with @RestController to indicate that it is a REST-ful controller.
 * It is mapped to the "/login" URL path and allows cross-origin requests from "<a href="http://localhost:4200">localhost:4200</a>".
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    /**
     * The UserService is injected to handle user-related operations.
     */
    @Autowired
    private UserService userService;

    /**
     * The AuthenticationManager is injected to handle authentication logic.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * The JwtService is injected to handle JWT token generation.
     */
    private final JwtService jwtService;

    /**
     * Constructor for AuthController.
     * @param userService the UserService to handle user-related operations
     * @param authenticationManager the AuthenticationManager to handle authentication logic
     * @param jwtService the JwtService to handle JWT token generation
     */
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    /**
     * This method handles GET requests to retrieve the login page.
     * @return ResponseEntity containing a message indicating the login page
     */
    @GetMapping("/")
    public ResponseEntity<String> getLoginPage() {
        return new ResponseEntity<>("Login Page", HttpStatus.OK);
    }

    /**
     * This method handles POST requests to authenticate a user and generate a JWT token.
     * It expects a request body containing a Map with "params" key, which should contain "email" and "password" keys.
     * @param request the request body containing user credentials
     * @return ResponseEntity containing the JWT token and user information if authentication is successful
     * @throws CreationException if the request format is invalid or if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,Object> request) throws CreationException {

        if(request == null || !request.containsKey("params")) {
            throw new CreationException("Invalid request format. 'params' key is missing or request is null.");
        }

        if(!(request.get("params") instanceof Map)) {
            throw new CreationException("'params' should be a Map containing 'email' and 'password'.");
        }

        @SuppressWarnings("unchecked")
        Map<String,Object> params = (Map<String, Object>) request.get("params");

        String email = (String) params.get("email");
        String password = (String) params.get("password");

        if(email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new CreationException("Invalid email or password.");
        }

        User user = userService.getUserByEmail(email);

        if (user == null) {
            throw new CreationException("User not found.");
        }

        // Check if the password matches the stored password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Authenticate the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

        // Perform the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtService.generateToken(email);

        if(token == null || token.isEmpty()) {
            throw new CreationException("Failed to generate JWT token.");
        }

        Map<String,Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of("name",user.getName() ,"email", email));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * This method handles POST requests to register a new user.
     * @param request the request body containing user details
     * @return ResponseEntity containing the created UserDTO if registration is successful
     * @throws ObjectNotFound if the request format is invalid or if user details are missing
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO request) throws ObjectNotFound {
        User user = new User(request.getName(), request.getEmail(), request.getPassword());

        if(user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new ObjectNotFound("Invalid user details. Name, email, and password are required.");
        }

        UserDTO createdUser = userService.addUser(user);

        if(createdUser == null) {
            throw new ObjectNotFound("Failed to create user.");
        }

        String token = jwtService.generateToken(createdUser.getEmail());

        Map<String,Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
