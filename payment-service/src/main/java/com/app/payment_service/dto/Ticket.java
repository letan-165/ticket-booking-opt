package com.app.payment_service.dto;

import com.app.ticket_common_library.common.enums.TicketStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ticket {
    Integer id;
    String userId;
    Integer seatId;
    Integer price;
    LocalDateTime bookingTime;
    TicketStatus status;
}
