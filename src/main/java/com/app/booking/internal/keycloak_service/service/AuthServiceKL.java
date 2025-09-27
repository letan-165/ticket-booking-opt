package com.app.booking.internal.keycloak_service.service;

import com.app.booking.internal.keycloak_service.client.KeycloakClient;
import com.app.booking.internal.keycloak_service.mapper.KeycloakMapper;
import com.app.booking.internal.keycloak_service.model.dto.request.CreateUserRequest;
import com.app.booking.internal.keycloak_service.model.dto.request.LoginRequest;
import com.app.booking.internal.keycloak_service.model.dto.response.LoginResponse;
import com.app.booking.internal.keycloak_service.model.keycloak.CreateUser;
import com.app.booking.internal.keycloak_service.model.keycloak.RoleKeycloak;
import com.app.booking.internal.keycloak_service.model.keycloak.UserInfoKeyCloak;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceKL {
    KeycloakClient keycloakClient;
    KeycloakMapper keycloakMapper;

    public LoginResponse login(LoginRequest loginRequest) {
        return keycloakClient.login(loginRequest);
    }

    public String createUser(CreateUserRequest request) {
        LoginResponse response = keycloakClient.clientCredentialsLogin();
        String token = response.getAccess_token();

        CreateUser payload = keycloakMapper.toCreateUser(request);
        payload = payload.toBuilder()
                .enabled(true)
                .credentials(List.of(CreateUser.Credential.builder()
                        .type("password")
                        .value("1")
                        .temporary(false)
                        .build()))
                .build();

        String userID = keycloakClient.createUser(token, payload);
        keycloakClient.assignRole(token, userID, List.of(RoleKeycloak.builder()
                .id("a6568ee2-bec1-4cff-9a70-eef8641eda06")
                .name("USER")
                .build()));
        return userID;
    }

    public UserInfoKeyCloak userInfo(String token) {
        return keycloakClient.userInfo(token);
    }
}
