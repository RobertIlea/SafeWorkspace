/**
 * JwtAuthFilter.java
 * This file is part of the Spring Project.
 * This class is a filter that intercepts HTTP requests to check for JWT authentication.
 * It extracts the JWT from the Authorization header, validates it, and sets the authentication in the security context if valid.
 * It uses the JwtService to handle JWT operations and UserService to retrieve user details.
 * It extends OncePerRequestFilter to ensure it is executed once per request.
 * It is used to secure endpoints by checking if the user is authenticated based on the JWT token.
 * It is typically used in conjunction with Spring Security to protect RESTful APIs.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springproject.entity.User;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthFilter is a filter that checks for JWT authentication in incoming HTTP requests.
 * It extracts the JWT from the Authorization header, validates it, and sets the authentication in the security context if valid.
 */
public class JwtAuthFilter extends OncePerRequestFilter {
    /**
     * JwtService is used to handle JWT operations.
     */
    private final JwtService jwtService;

    /**
     * UserService is used to retrieve user details based on the email extracted from the JWT.
     */
    private final UserService userService;

    /**
     * Constructor for JwtAuthFilter.
     * @param jwtService the JwtService used for JWT operations.
     * @param userService the UserService used to retrieve user details.
     */
    public JwtAuthFilter(final JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * This method is called for each HTTP request to check for JWT authentication.
     * It extracts the JWT from the Authorization header, validates it, and sets the authentication in the security context if valid.
     * If the JWT is not present or invalid, it allows the request to proceed without authentication.
     *
     * @param request the HttpServletRequest object containing the request details.
     * @param response the HttpServletResponse object used to send a response.
     * @param filterChain the FilterChain to continue processing the request.
     * @throws ServletException if an error occurs during filtering.
     * @throws IOException if an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractEmail(jwt);

            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if(jwtService.validateToken(jwt)){
                    User user = userService.getUserByEmail(userEmail);

                    if(user != null) {
                        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEmail,null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Authentication set for user " + userEmail);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("Authentication error: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
