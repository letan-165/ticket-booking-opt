package com.app.booking.event_service_test.integration;

import com.app.booking.common.AbstractIntegrationTest;
import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.internal.event_service.dto.request.EventRequest;
import com.app.booking.internal.event_service.dto.request.SeatStatusRequest;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.EventRepository;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
class EventIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    VNPayService vnPayService;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    SeatRepository seatRepository;

    Event event;

    @BeforeEach
    void initData() {
        event = eventRepository.findAll(Pageable.ofSize(1)).getContent().get(0);
    }

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get("/events/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void findAllByOrganizerId_success() throws Exception {
        mockMvc.perform(get("/events/public/organizers/{id}", event.getOrganizerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void getDetail_success() throws Exception {
        mockMvc.perform(get("/events/public/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(event.getId()))
                .andExpect(jsonPath("result.organizerId").value(event.getOrganizerId()))
                .andExpect(jsonPath("result.name").value(event.getName()))
                .andExpect(jsonPath("result.location").value(event.getLocation()))
                .andExpect(jsonPath("result.priceTicket").value(event.getPriceTicket()))
                .andExpect(jsonPath("result.totalSeats").value(event.getTotalSeats()))
                .andExpect(jsonPath("result.seats.length()").value(event.getTotalSeats()));
    }

    @Test
    void create_success() throws Exception {
        EventRequest request = RequestMock.eventMock(10);
        request.setOrganizerId(event.getOrganizerId());

        mockMvc.perform(post("/events/public")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id", not(emptyOrNullString())))
                .andExpect(jsonPath("result.organizerId").value(request.getOrganizerId()))
                .andExpect(jsonPath("result.name").value(request.getName()))
                .andExpect(jsonPath("result.location").value(request.getLocation()))
                .andExpect(jsonPath("result.priceTicket").value(request.getPriceTicket()))
                .andExpect(jsonPath("result.totalSeats").value(request.getTotalSeats()))
                .andExpect(jsonPath("result.seats.length()").value(request.getTotalSeats()));

    }

    @Test
    void create_fail_USER_NO_EXISTS() throws Exception {
        EventRequest request = RequestMock.eventMock(10);
        request.setOrganizerId("fake");

        ErrorCode errorCode = ErrorCode.USER_NO_EXISTS;
        mockMvc.perform(post("/events/public")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }

    @Test
    void update_success() throws Exception {
        String nameUpdate = event.getName() + "update";
        String locationUpdate = event.getLocation() + "update";
        EventRequest request = EventRequest.builder()
                .name(nameUpdate)
                .location(locationUpdate)
                .build();

        mockMvc.perform(patch("/events/public/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(event.getId()))
                .andExpect(jsonPath("result.organizerId").value(event.getOrganizerId()))
                .andExpect(jsonPath("result.name").value(nameUpdate))
                .andExpect(jsonPath("result.location").value(locationUpdate))
                .andExpect(jsonPath("result.priceTicket").value(event.getPriceTicket()))
                .andExpect(jsonPath("result.totalSeats").value(event.getTotalSeats()));
    }

    @Test
    void update_fail_EVENT_NO_EXISTS() throws Exception {
        Integer eventId = 999999999;
        String nameUpdate = event.getName() + "update";
        String locationUpdate = event.getLocation() + "update";
        EventRequest request = EventRequest.builder()
                .name(nameUpdate)
                .location(locationUpdate)
                .build();


        ErrorCode errorCode = ErrorCode.EVENT_NO_EXISTS;
        mockMvc.perform(patch("/events/public/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }

    @Test
    void delete_success() throws Exception {
        Integer eventID = event.getId();
        mockMvc.perform(delete("/events/public/{id}", eventID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result").value(true));

        assertFalse(eventRepository.existsById(eventID));
    }

    @Test
    void delete_fail_EVENT_NO_EXISTS() throws Exception {
        Integer eventID = 999999999;
        ErrorCode errorCode = ErrorCode.EVENT_NO_EXISTS;
        mockMvc.perform(delete("/events/public/{id}", eventID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }


    @Test
    void updateStatusSeats_success() throws Exception {
        Seat seat = seatRepository.findAllByEventId(event.getId()).get(0);
        SeatStatus statusUpdate = SeatStatus.LOCKED;
        SeatStatusRequest request = SeatStatusRequest.builder()
                .status(statusUpdate)
                .build();

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
        mockMvc.perform(patch("/events/public/seats/{seatId}", seat.getId()).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(seat.getId()))
                .andExpect(jsonPath("result.eventId").value(seat.getEventId()))
                .andExpect(jsonPath("result.seatNumber").value(seat.getSeatNumber()))
                .andExpect(jsonPath("result.status").value(statusUpdate.name()));

        assertThat(seat.getStatus()).isEqualTo(statusUpdate);
    }


}
