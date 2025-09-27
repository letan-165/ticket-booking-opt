package com.app.booking.internal.keycloak_service.service;

import com.app.booking.internal.keycloak_service.client.KeycloakClient;
import com.app.booking.internal.keycloak_service.model.dto.request.UpdateUserRequest;
import com.app.booking.internal.keycloak_service.model.dto.response.LoginResponse;
import com.app.booking.internal.keycloak_service.model.keycloak.UserInfoKeyCloak;
import com.app.booking.internal.keycloak_service.model.keycloak.UserKeycloak;
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
public class UserServiceKL {
    KeycloakClient keycloakClient;

    public List<UserKeycloak> getUsers(String token, String username, int first, int max) {
        return keycloakClient.getUsers(token, username, first, max);
    }

    public String update(String token, UpdateUserRequest request) {
        request.setEnabled(true);
        LoginResponse response = keycloakClient.clientCredentialsLogin();
        String tokenAdmin = response.getAccess_token();

        UserInfoKeyCloak user = keycloakClient.userInfo(token);

        keycloakClient.update(tokenAdmin, user.getSub(), request);
        return user.getSub();
    }

}
