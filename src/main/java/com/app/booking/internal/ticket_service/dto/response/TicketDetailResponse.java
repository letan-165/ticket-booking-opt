package com.app.booking.internal.ticket_service.dto.response;

import com.app.booking.common.enums.TicketStatus;
import com.app.booking.internal.event_service.entity.Seat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketDetailResponse {
    Long id;
    String userId;
    Seat seat;
    LocalDateTime bookingTime;
    TicketStatus status;
}
