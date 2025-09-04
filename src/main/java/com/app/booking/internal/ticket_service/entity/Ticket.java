package com.app.booking.internal.ticket_service.entity;

import com.app.booking.common.enums.SeatStatus;
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
    Long id;
    @Column(name = "user_id", nullable = false)
    String userId;
    @Column(name = "seat_id", nullable = false)
    Long seatId;
    @Column(name = "booking_time")
    LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    SeatStatus status;
}
