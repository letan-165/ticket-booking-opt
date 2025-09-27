package com.app.booking.internal.keycloak_service.mapper;

import com.app.booking.internal.keycloak_service.model.dto.request.CreateUserRequest;
import com.app.booking.internal.keycloak_service.model.keycloak.CreateUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeycloakMapper {
    CreateUser toCreateUser(CreateUserRequest request);

}
