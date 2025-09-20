package com.app.booking.user_service_test.model_mock;

import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.user_service.dto.request.UserRequest;

public class RequestMock {
    public static UserRequest userMock(){
        return UserRequest.builder()
                .name("name")
                .email("email")
                .password("1")
                .role(UserRole.USER)
                .build();
    }

    public static UserRequest userMock(String password){
        return UserRequest.builder()
                .name("name")
                .email("email")
                .password(password)
                .role(UserRole.USER)
                .build();
    }
}
