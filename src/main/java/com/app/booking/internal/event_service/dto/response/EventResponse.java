package com.app.booking.internal.event_service.dto.response;

import com.app.booking.internal.event_service.entity.Seat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponse {
    Integer id;
    String organizerId;
    String name;
    String location;
    Integer priceTicket;
    LocalDateTime time;
    Integer totalSeats;
    List<Seat> seats;
}
