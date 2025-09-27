package com.app.booking.internal.keycloak_service.model.keycloak;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserKeycloak {
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    boolean enabled;
}

