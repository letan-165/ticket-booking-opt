package com.app.booking.internal.keycloak_service.model.dto.request;

import com.app.booking.common.log.Maskable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest implements Maskable {
    @NotBlank
    String username;

    @NotBlank
    String password;

    @Override
    public Object maskSensitive() {
        return new LoginRequest(username, "****");
    }
}
