package com.app.user_service.model.keycloak;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CreateUser extends UserKeycloak {
    List<Credential> credentials;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credential {
        String type;
        String value;
        boolean temporary;
    }
}
