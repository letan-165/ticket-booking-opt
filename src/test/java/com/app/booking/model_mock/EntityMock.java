package com.app.booking.model_mock;

import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.user_service.entity.User;

public class EntityMock {
    public static User userMock(){
        return User.builder()
                .id("userID")
                .name("name")
                .email("email")
                .password("1")
                .role(UserRole.USER)
                .build();
    }
}
