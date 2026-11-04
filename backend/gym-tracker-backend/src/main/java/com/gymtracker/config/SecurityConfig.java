package com.gymtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Environment environment) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource(environment)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/members/register", "/api/members/login", "/api/members/logout", "/actuator/health").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource(Environment environment) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(environment.getProperty(
                "app.cors.allowed-origins",
                "http://localhost:5500,http://127.0.0.1:5500").split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}