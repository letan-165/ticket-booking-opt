package com.app.booking.model_mock;

import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.event_service.dto.response.EventResponse;
import com.app.booking.internal.user_service.dto.response.LoginResponse;
import com.app.booking.internal.user_service.dto.response.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

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

    public static EventResponse eventMock(){
        return EventResponse.builder()
                .id(1)
                .organizerId("organizerId")
                .name("name")
                .location("location")
                .priceTicket(10000)
                .time(LocalDateTime.now())
                .totalSeats(0)
                .seats(List.of())
                .build();
    }
}
