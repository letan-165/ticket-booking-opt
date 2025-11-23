package com.app.ticket_service.dto;

import com.app.ticket_common_library.common.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    Integer id;
    Integer ticketId;
    Integer amount;
    PaymentStatus status;
    LocalDateTime createdAt;
}
