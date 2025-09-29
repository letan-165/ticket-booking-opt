package com.app.booking.internal.keycloak_service.service;

import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    KeycloakClient keycloakClient;
    KeycloakMapper keycloakMapper;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            return keycloakClient.login(loginRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
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
        String userID;
        try {
            userID = keycloakClient.createUser(token, payload);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.USER_EXISTS);
        }
        keycloakClient.assignRole(token, userID, List.of(RoleKeycloak.builder()
                .id("a6568ee2-bec1-4cff-9a70-eef8641eda06")
                .name("USER")
                .build()));

        return userID;
    }

    public UserInfoKeyCloak userInfo() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return keycloakClient.userInfo(jwt.getTokenValue());
    }

    public void checkUserToken(String userId) {
        var user = userInfo();
        if (!userId.equals(user.getSub()))
            throw new AppException(ErrorCode.USER_INVALID);
    }
}
