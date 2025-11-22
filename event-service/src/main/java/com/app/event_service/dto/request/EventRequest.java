package com.app.event_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequest {
    @NotBlank
    String organizerId;

    @NotBlank
    String name;
    Integer priceTicket;

    String location;
    LocalDateTime time;
    int totalSeats;
}
