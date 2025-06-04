/**
 * OAuth2SuccessHandler.java
 * This class handles successful OAuth2 authentication.
 * It retrieves user information from the OAuth2 provider.
 * If the user does not exist in the system, it creates a new user with a random password.
 * It then generates a JWT token and redirects the user to a specified URL with the token and user information.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.handler;

import jakarta.servlet.ServletException;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.User;
import org.example.springproject.service.JwtService;
import org.example.springproject.util.PasswordGenerator;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2SuccessHandler is responsible for handling successful OAuth2 authentication.
 * It checks if the user exists in the system, creates a new user if not, generates a JWT token and redirects the user to a specified URL with the token and user information.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /**
     * Autowired services for JWT generation and user management.
     */
    @Autowired
    private JwtService jwtService;

    /**
     * Autowired service for user management.
     */
    @Autowired
    private UserService userService;

    /**
     * Password encoder for encoding passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Default constructor initializing the password encoder.
     */
    public OAuth2SuccessHandler() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Handles successful authentication by retrieving user information from the OAuth2 provider.
     * If the user does not exist, it creates a new user with a random password.
     * It then generates a JWT token and redirects the user to a specified URL with the token and user information.
     * @param request the HttpServletRequest that contains the request data
     * @param response the HttpServletResponse that will contain the response data
     * @param authentication the Authentication object containing user details
     * @throws IOException if an input or output error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try{
            System.out.println("onAuthenticationSuccess...");
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            User user = null;

            try{
                user = userService.getUserByEmail(email);
                System.out.println(user.getEmail());
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

            if(user == null){
                String name = oAuth2User.getAttribute("name");
                String randomPassword = PasswordGenerator.generateRandomPassword(16);
                userService.addUser(new User(name,email,randomPassword,null));
            }
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setName(oAuth2User.getAttribute("name"));
            String userJson = userDTO.toString().replace("\"", "'");

            String jwt = jwtService.generateToken(email);
            String redirectUrl = "http://localhost:4200/callback?jwtToken=" + jwt + "&user=" + userJson;
            response.sendRedirect(redirectUrl);
        }catch (IOException e){
            throw new IOException("Error during authentication success handling: ", e);
        }
    }
}
