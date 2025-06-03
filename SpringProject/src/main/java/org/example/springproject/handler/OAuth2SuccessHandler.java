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

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    private final PasswordEncoder passwordEncoder;

    public OAuth2SuccessHandler() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
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
    }
}
