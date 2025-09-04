package com.app.booking.internal.event_service.dto.request;

import com.app.booking.common.enums.SeatStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatStatusRequest {
    SeatStatus status;
}
