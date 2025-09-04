package com.app.booking.internal.user_service.dto.response;

import com.app.booking.common.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String name;
    String email;
    UserRole role;
}
