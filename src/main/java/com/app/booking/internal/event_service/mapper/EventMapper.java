package com.app.booking.internal.event_service.mapper;

import com.app.booking.internal.event_service.dto.request.EventRequest;
import com.app.booking.internal.event_service.dto.response.EventResponse;
import com.app.booking.internal.event_service.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    Event toEvent(EventRequest request);
    EventResponse toEventResponse(Event event);
    void updateEventFromRequest(Event event, @MappingTarget EventRequest request);
}
