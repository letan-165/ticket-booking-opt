package com.app.event_service.entity;

import com.app.ticket_common_library.common.enums.SeatStatus;
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
    Integer id;
    @Column(name = "event_id", nullable = false)
    Integer eventId;

    @Column(name = "seat_number", nullable = false)
    String seatNumber;

    @Enumerated(EnumType.STRING)
    SeatStatus status;
}
