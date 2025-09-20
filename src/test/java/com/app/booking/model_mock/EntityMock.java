package com.app.booking.model_mock;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.user_service.entity.User;

import java.time.LocalDateTime;

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

    public static Ticket ticketMock(){
        return Ticket.builder()
                .id(1)
                .userId("user123")
                .seatId(10)
                .price(100000)
                .bookingTime(LocalDateTime.now())
                .status(TicketStatus.BOOKED)
                .build();
    }

    public static Payment paymentMock(){
        return Payment.builder()
                .id(2)
                .ticketId(1)
                .createdAt(LocalDateTime.now())
                .amount(100000)
                .status(PaymentStatus.PENDING)
                .build();
    }
}
