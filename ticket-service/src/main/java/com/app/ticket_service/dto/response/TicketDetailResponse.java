package com.app.ticket_service.dto.response;

import com.app.ticket_common_library.common.enums.TicketStatus;
import com.app.ticket_service.dto.Payment;
import com.app.ticket_service.dto.Seat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketDetailResponse {
    Integer id;
    String userId;
    String organizerId;
    Seat seat;
    Integer price;
    LocalDateTime bookingTime;
    TicketStatus status;
    Payment payment;
}
