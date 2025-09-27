package com.app.booking.internal.keycloak_service.model.keycloak;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleKeycloak {
    String id;
    String name;
}
