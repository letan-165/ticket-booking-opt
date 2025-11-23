package com.app.ticket_service.dto;

import com.app.ticket_common_library.common.enums.SeatStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat {
    Integer id;
    Integer eventId;
    String seatNumber;
    SeatStatus status;
}
