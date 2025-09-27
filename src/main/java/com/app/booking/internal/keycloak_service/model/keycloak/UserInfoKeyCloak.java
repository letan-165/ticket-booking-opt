package com.app.booking.internal.keycloak_service.model.keycloak;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoKeyCloak {
    String sub;
    Boolean email_verified;
    String name;
    String preferred_username;
    String given_name;
    String family_name;
    String email;
}
