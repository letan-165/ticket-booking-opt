package com.app.booking.internal.user_service.dto.request;

import com.app.booking.common.enums.UserRole;
import com.app.booking.common.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @NotBlank
    String name;

    @NotBlank
    String password;

    @NotBlank
    @Email
    String email;

    @NotNull
    UserRole role;
}
