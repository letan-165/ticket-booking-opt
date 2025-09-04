package com.app.booking.internal.event_service.dto.request;

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

    String location;
    LocalDateTime time;
    long totalSeats;
}
