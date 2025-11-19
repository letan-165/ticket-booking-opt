package com.app.user_service.mapper;

import com.app.user_service.model.dto.request.CreateUserRequest;
import com.app.user_service.model.keycloak.CreateUser;
import com.app.user_service.model.keycloak.UserInfoKeyCloak;
import com.app.user_service.model.keycloak.UserKeycloak;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KeycloakMapper {
    CreateUser toCreateUser(CreateUserRequest request);

    @Mapping(source = "sub", target = "id")
    @Mapping(source = "preferred_username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "family_name", target = "lastName")
    @Mapping(source = "given_name", target = "firstName")
    UserKeycloak toUserKeycloak(UserInfoKeyCloak user);
}
