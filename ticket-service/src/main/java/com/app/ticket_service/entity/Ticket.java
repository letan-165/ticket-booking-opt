package com.app.ticket_service.entity;

import com.app.ticket_common_library.common.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "seat_id", nullable = false)
    Integer seatId;

    @Column(nullable = false)
    Integer price;

    @Column(name = "booking_time")
    LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    TicketStatus status;
}
