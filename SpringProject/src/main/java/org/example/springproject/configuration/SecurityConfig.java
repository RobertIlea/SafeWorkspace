/**
 * Security configuration for the Spring project.
 * This configuration sets up CORS, CSRF protection, and authentication mechanisms.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.configuration;

import org.example.springproject.handler.OAuth2SuccessHandler;
import org.example.springproject.service.JwtService;
import org.example.springproject.service.UserService;
import org.example.springproject.util.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class that sets up CORS, CSRF protection, and authentication mechanisms.
 * It uses a custom OAuth2 success handler and JWT authentication filter.
 * This class is annotated with @Configuration and @EnableWebSecurity to enable Spring Security features.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * The OAuth2 success handler that processes successful OAuth2 login attempts.
     */
    private final OAuth2SuccessHandler successHandler;

    /**
     * Constructor for SecurityConfig that injects the OAuth2 success handler.
     * @param successHandler the OAuth2 success handler to be used in the security configuration
     */
    @Autowired
    public SecurityConfig(OAuth2SuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    /**
     * Configures CORS settings for the application.
     * It is annotated with @Bean to make it available in the Spring context.
     * @return a CorsConfigurationSource that defines allowed origins, methods, headers, and credentials
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set the allowed origin
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow all REST methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow specific headers
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Allow credentials to be included in requests
        configuration.setAllowCredentials(true);

        // Create a UrlBasedCorsConfigurationSource to register the CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Register the CORS configuration for all paths
        source.registerCorsConfiguration("/**", configuration);

        // Return the configured source
        return source;
    }

    /**
     * Configures the security filter chain for the application.
     * This method sets up CORS, CSRF protection, and authentication mechanisms.
     * It uses a custom JWT authentication filter and an OAuth2 success handler.
     * This method is annotated with @Bean to make it available in the Spring context.
     * @param http the HttpSecurity object used to configure security settings
     * @param jwtService the JWT service used for authentication
     * @param userService the user service used for user-related operations
     * @return a SecurityFilterChain that defines the security rules for the application
     * @throws Exception if an error occurs during security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, UserService userService) throws Exception {
           return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**", "/error", "/user/**", "/home/**", "/room/**", "/sensor/**", "/alerts/**","/custom-alert/**","/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtService,userService), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage("/auth/")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler)
                        .permitAll()
                )

                .logout(logout -> logout.logoutSuccessUrl("/login"))
                .build();
    }

    /**
     * Creates an AuthenticationManager bean that provides authentication capabilities.
     * This method is annotated with @Bean to make it available in the Spring context.
     * @param config the AuthenticationConfiguration used to create the AuthenticationManager
     * @return an AuthenticationManager that can be used for authentication
     * @throws Exception if an error occurs while creating the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}