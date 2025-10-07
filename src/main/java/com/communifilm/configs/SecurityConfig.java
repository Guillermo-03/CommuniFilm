package com.communifilm.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GoogleAuthFilter googleAuthFilter;

    public SecurityConfig(GoogleAuthFilter googleAuthFilter) {
        this.googleAuthFilter = googleAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login").permitAll() // Allow anyone to access the signup endpoint
                        .requestMatchers("/movies/**").permitAll()
                        .requestMatchers("/reviews/**").permitAll()
                        .anyRequest().authenticated()   // All other endpoints require authentication
                )
                .addFilterBefore(googleAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
