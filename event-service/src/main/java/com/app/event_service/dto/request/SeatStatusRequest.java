package com.app.event_service.dto.request;

import com.app.ticket_common_library.common.enums.SeatStatus;
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
