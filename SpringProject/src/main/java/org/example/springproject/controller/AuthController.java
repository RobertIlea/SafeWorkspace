package org.example.springproject.controller;

import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.UserService;
import org.example.springproject.util.UserMapper;
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
import java.util.logging.Logger;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:4200")

public class AuthController {
    @Autowired
    private UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @GetMapping("/")
    public ResponseEntity<String> getLoginPage() {
        return ResponseEntity.ok("Login page");
    }
    @PostMapping("/")
    public ResponseEntity<?> login(@RequestBody Map<String,Object> request) {
        System.out.println("Login request received");
        System.out.println(request);

        @SuppressWarnings("unchecked")
        Map<String,Object> params = (Map<String, Object>) request.get("params");

        String email = (String) params.get("email");
        String password = (String) params.get("password");

        System.out.println(email);
        System.out.println(password);
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken(email);
        Map<String,Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of("name",user.getName() ,"email", email));
        return ResponseEntity.ok(response);

    }

}
