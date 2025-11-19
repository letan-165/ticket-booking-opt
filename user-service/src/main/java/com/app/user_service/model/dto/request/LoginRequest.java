package com.app.user_service.model.dto.request;

import com.app.user_service.config.log.Maskable;
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
