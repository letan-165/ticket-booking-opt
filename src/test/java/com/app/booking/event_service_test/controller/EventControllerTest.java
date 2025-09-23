package com.app.booking.event_service_test.controller;

import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.EntityMock;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.common.model_mock.ResponseMock;
import com.app.booking.internal.event_service.controller.EventController;
import com.app.booking.internal.event_service.dto.request.EventRequest;
import com.app.booking.internal.event_service.dto.request.SeatStatusRequest;
import com.app.booking.internal.event_service.dto.response.EventResponse;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    EventService eventService;

    Event event;
    EventResponse eventResponse;
    List<Event> events;
    DateTimeFormatter formatter;

    @BeforeEach
    void initData() {
        event = EntityMock.eventMock();
        events = new ArrayList<>();
        events.add(event);
        events.add(event);
        events.add(event);
        eventResponse = ResponseMock.eventMock();
        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    @Test
    void getAll_success() throws Exception {
        when(eventService.getAll(any(Pageable.class))).thenReturn(events);

        mockMvc.perform(get("/events/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void findAllByOrganizerId_success() throws Exception {
        String organizerId = event.getOrganizerId();
        when(eventService.findAllByOrganizerId(eq(organizerId), any(Pageable.class))).thenReturn(events);

        mockMvc.perform(get("/events/public/organizers/{id}", organizerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void getDetail_success() throws Exception {
        Integer eventId = event.getId();
        when(eventService.getDetail(eventId)).thenReturn(eventResponse);

        mockMvc.perform(get("/events/public/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(eventResponse.getId()))
                .andExpect(jsonPath("result.organizerId").value(eventResponse.getOrganizerId()))
                .andExpect(jsonPath("result.name").value(eventResponse.getName()))
                .andExpect(jsonPath("result.location").value(eventResponse.getLocation()))
                .andExpect(jsonPath("result.priceTicket").value(eventResponse.getPriceTicket()))
                .andExpect(jsonPath("result.time").value(eventResponse.getTime().format(formatter)))
                .andExpect(jsonPath("result.totalSeats").value(eventResponse.getTotalSeats()))
                .andExpect(jsonPath("result.seats.length()").value(eventResponse.getSeats().size()));
    }

    @Test
    void create_success() throws Exception {
        EventRequest request = RequestMock.eventMock(0);
        var content = objectMapper.writeValueAsString(request);
        when(eventService.create(request)).thenReturn(eventResponse);

        mockMvc.perform(post("/events/public")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(eventResponse.getId()))
                .andExpect(jsonPath("result.organizerId").value(eventResponse.getOrganizerId()))
                .andExpect(jsonPath("result.name").value(eventResponse.getName()))
                .andExpect(jsonPath("result.location").value(eventResponse.getLocation()))
                .andExpect(jsonPath("result.priceTicket").value(eventResponse.getPriceTicket()))
                .andExpect(jsonPath("result.time").value(eventResponse.getTime().format(formatter)))
                .andExpect(jsonPath("result.totalSeats").value(eventResponse.getTotalSeats()))
                .andExpect(jsonPath("result.seats.length()").value(eventResponse.getSeats().size()));
    }

    @Test
    void create_fail_blank() throws Exception {
        ErrorCode errorCode = ErrorCode.NOT_BLANK;
        EventRequest request = EventRequest.builder()
                .organizerId("")
                .name("name")
                .build();
        var content = objectMapper.writeValueAsString(request);

        when(eventService.create(request)).thenReturn(eventResponse);
        String finalMessage = errorCode.getMessage().replace("{field}", "organizerId");
        mockMvc.perform(post("/events/public")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(finalMessage));

    }

    @Test
    void update_success() throws Exception {
        Integer eventId = event.getId();
        EventRequest request = RequestMock.eventMock(0);
        var content = objectMapper.writeValueAsString(request);

        when(eventService.update(eventId, request)).thenReturn(event);

        mockMvc.perform(patch("/events/public/{id}", eventId).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(event.getId()))
                .andExpect(jsonPath("result.organizerId").value(event.getOrganizerId()))
                .andExpect(jsonPath("result.name").value(event.getName()))
                .andExpect(jsonPath("result.location").value(event.getLocation()))
                .andExpect(jsonPath("result.priceTicket").value(event.getPriceTicket()))
                .andExpect(jsonPath("result.time").value(event.getTime().format(formatter)))
                .andExpect(jsonPath("result.totalSeats").value(event.getTotalSeats()));
    }

    @Test
    void delete_success() throws Exception {
        Integer eventId = event.getId();

        doNothing().when(eventService).delete(eventId);

        mockMvc.perform(delete("/events/public/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result").value(true));
    }

    @Test
    void updateStatusSeats_success() throws Exception {
        Seat seat = EntityMock.seatMock();
        SeatStatusRequest request = new SeatStatusRequest();
        var content = objectMapper.writeValueAsString(request);

        when(eventService.updateStatusSeats(seat.getId(), request)).thenReturn(seat);

        mockMvc.perform(patch("/events/public/seats/{seatId}", seat.getId()).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(seat.getId()))
                .andExpect(jsonPath("result.eventId").value(seat.getEventId()))
                .andExpect(jsonPath("result.seatNumber").value(seat.getSeatNumber()))
                .andExpect(jsonPath("result.status").value(seat.getStatus().name()));
    }

}
