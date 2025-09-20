package com.app.booking.user_service_test.model_mock;

import com.app.booking.common.enums.UserRole;
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
}
