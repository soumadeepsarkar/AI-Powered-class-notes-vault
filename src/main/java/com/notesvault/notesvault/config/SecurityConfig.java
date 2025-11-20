package com.notesvault.notesvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer; // Import required for Customizer.withDefaults()
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF for stateless REST APIs (Modern Syntax)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configure request authorization
                .authorizeHttpRequests(authorize -> authorize
                        // Allow unauthenticated access to all notes API endpoints
                        .requestMatchers("/api/notes/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // 3. Enable HTTP Basic Authentication
                // This allows users to authenticate via 'Authorization: Basic ...' header
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}