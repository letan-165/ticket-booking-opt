package com.app.booking.common.model_mock;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class EntityMock {
    public static User userMock(){
        return User.builder()
                .id("userID")
                .name("name")
                .email("email@email")
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

    public static Event eventMock(){
        return Event.builder()
                .id(1)
                .organizerId("organizerId")
                .name("name")
                .location("location")
                .priceTicket(10000)
                .time(LocalDateTime.now())
                .totalSeats(0)
                .build();
    }

    public static Seat seatMock(){
        return Seat.builder()
                .id(1)
                .eventId(2)
                .seatNumber("G1")
                .status(SeatStatus.LOCKED)
                .build();
    }
}
