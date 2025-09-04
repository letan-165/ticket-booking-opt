package com.app.booking.internal.event_service.entity;

import com.app.booking.common.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "seats")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "seat_number", nullable = false)
    String seatNumber;

    @Enumerated(EnumType.STRING)
    SeatStatus status;
}
