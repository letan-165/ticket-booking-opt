package com.app.ticket_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    Integer id;
    String organizerId;
    String name;
    String location;
    Integer priceTicket;
    LocalDateTime time;
    Integer totalSeats;
}
