package com.app.payment_service.messaging.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMessaging {
    Integer paymentId;
    boolean paid;
}
