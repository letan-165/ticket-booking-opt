package com.app.booking.internal.ticket_service.mapper;

import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketDetailResponse toTicketDetailResponse(Ticket ticket);
}
