package com.app.booking.internal.ticket_service.dto.response;

import com.app.booking.common.enums.TicketStatus;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.payment_service.entity.Payment;
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
    Seat seat;
    Integer price;
    LocalDateTime bookingTime;
    TicketStatus status;
    Payment payment;
}
