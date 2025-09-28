package com.app.booking.internal.keycloak_service.service;

import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.keycloak_service.client.KeycloakClient;
import com.app.booking.internal.keycloak_service.mapper.KeycloakMapper;
import com.app.booking.internal.keycloak_service.model.dto.request.UpdateUserRequest;
import com.app.booking.internal.keycloak_service.model.dto.response.LoginResponse;
import com.app.booking.internal.keycloak_service.model.keycloak.UserInfoKeyCloak;
import com.app.booking.internal.keycloak_service.model.keycloak.UserKeycloak;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceKL {
    KeycloakClient keycloakClient;
    KeycloakMapper keycloakMapper;

    @Cacheable(value = "users", keyGenerator = "keycloakKeyGenerator")
    public List<UserKeycloak> getUsers(String token, int first, int max) {
        return keycloakClient.getUsers(token, null, first, max);
    }

    @Cacheable(value = "user", key = "'getUser:' + #username")
    public UserKeycloak getUser(String token, String username) {
        var lists = keycloakClient.getUsers(token, username, 0, 2);
        if (lists.isEmpty())
            throw new AppException(ErrorCode.USER_NO_EXISTS);

        return lists.get(0);
    }

    @CachePut(value = "user", key = "'getUser:' + #result.username")
    @CacheEvict(value = "users", allEntries = true)
    public UserKeycloak update(String token, UpdateUserRequest request) {
        request.setEnabled(true);
        LoginResponse response = keycloakClient.clientCredentialsLogin();
        String tokenAdmin = response.getAccess_token();

        UserInfoKeyCloak user = keycloakClient.userInfo(token);

        keycloakClient.update(tokenAdmin, user.getSub(), request);
        UserKeycloak result = keycloakMapper.toUserKeycloak(keycloakClient.userInfo(token));
        result.setEnabled(request.isEnabled());
        return result;
    }

}
