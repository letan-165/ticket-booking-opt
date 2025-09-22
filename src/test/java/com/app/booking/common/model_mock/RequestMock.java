package com.app.booking.common.model_mock;

import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.event_service.dto.request.EventRequest;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.user_service.dto.request.LoginRequest;
import com.app.booking.internal.user_service.dto.request.UserRequest;

import java.time.LocalDateTime;

public class RequestMock {
    public static UserRequest userMock(){
        return UserRequest.builder()
                .name("name")
                .email("email@email")
                .password("1")
                .role(UserRole.USER)
                .build();
    }

    public static UserRequest userMock(String password){
        return UserRequest.builder()
                .name("name")
                .email("email@email")
                .password(password)
                .role(UserRole.USER)
                .build();
    }

    public static LoginRequest loginMock(){
        return LoginRequest.builder()
                .email("email@email")
                .password("1")
                .build();
    }

    public static EventRequest eventMock(int totalSeats){
        return EventRequest.builder()
                .organizerId("organizerId")
                .name("name")
                .location("location")
                .priceTicket(10000)
                .time(LocalDateTime.now())
                .totalSeats(totalSeats)
                .build();
    }

    public static BookRequest bockingMock(){
        return BookRequest.builder()
                .userId("userId")
                .seatId(1)
                .build();
    }
}
