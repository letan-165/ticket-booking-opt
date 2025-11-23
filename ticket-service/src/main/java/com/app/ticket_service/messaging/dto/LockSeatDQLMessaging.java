package com.app.ticket_service.messaging.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LockSeatDQLMessaging {
    Integer paymentID;
    Integer ticketID;
}
