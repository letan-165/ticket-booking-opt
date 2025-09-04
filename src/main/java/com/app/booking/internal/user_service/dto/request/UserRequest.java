package com.app.booking.internal.user_service.dto.request;

import com.app.booking.common.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String name;
    String password;
    String email;
    UserRole role;
}
