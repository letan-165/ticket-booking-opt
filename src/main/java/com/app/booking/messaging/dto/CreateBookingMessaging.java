package com.app.booking.messaging.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingMessaging {
    String userId;
    Integer seatId;
    Integer paymentId;
    Integer price;
}
