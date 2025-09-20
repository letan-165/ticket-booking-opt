package com.app.booking.model_mock;

import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.user_service.dto.response.LoginResponse;
import com.app.booking.internal.user_service.dto.response.UserResponse;

public class ResponseMock {
    public static UserResponse userMock(){
        return UserResponse.builder()
                .id("userID")
                .name("name")
                .email("email")
                .role(UserRole.USER)
                .build();
    }

    public static LoginResponse loginMock(){
        return LoginResponse.builder()
                .userID("userID")
                .name("name")
                .token("token")
                .build();
    }
}
