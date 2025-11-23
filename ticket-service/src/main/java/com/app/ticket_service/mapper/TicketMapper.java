package com.app.ticket_service.mapper;

import com.app.ticket_service.dto.response.TicketDetailResponse;
import com.app.ticket_service.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketDetailResponse toTicketDetailResponse(Ticket ticket);
}
