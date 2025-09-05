package com.app.booking.internal.event_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "organizer_id", nullable = false)
    String organizerId;
    String name;
    String location;

    @Column(name = "price_ticket")
    Integer priceTicket;

    @Column(name = "time")
    LocalDateTime time;

    @Column(name = "total_seats")
    Integer totalSeats;
}
