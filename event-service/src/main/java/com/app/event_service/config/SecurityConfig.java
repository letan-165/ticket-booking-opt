package com.app.event_service.config;

import com.app.ticket_common_library.config.security.AuthEntryPoint;
import com.app.ticket_common_library.config.security.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    String[] endpoints = {"/events/public/guest/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(endpoints).permitAll()
                .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                        .jwtAuthenticationConverter(new KeycloakRoleConverter())
                ).authenticationEntryPoint(new AuthEntryPoint()));

        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}
