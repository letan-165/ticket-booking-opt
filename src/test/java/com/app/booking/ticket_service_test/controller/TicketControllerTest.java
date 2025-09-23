package com.app.booking.ticket_service_test.controller;

import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.EntityMock;
import com.app.booking.internal.ticket_service.controller.TicketController;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.service.TicketService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TicketService ticketService;

    Ticket ticket;
    List<Ticket> tickets;
    DateTimeFormatter formatter;

    @BeforeEach
    void initData() {
        ticket = EntityMock.ticketMock();
        tickets = new ArrayList<>();
        tickets.add(ticket);
        tickets.add(ticket);
        tickets.add(ticket);
        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    @Test
    void getAll_success() throws Exception {
        when(ticketService.findAll(any(Pageable.class))).thenReturn(tickets);

        mockMvc.perform(get("/tickets/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void getByUser_success() throws Exception {
        String userId = ticket.getUserId();
        when(ticketService.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(tickets);

        mockMvc.perform(get("/tickets/public/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void getDetail_success() throws Exception {
        Integer ticketId = ticket.getId();
        TicketDetailResponse response = TicketDetailResponse.builder()
                .id(1)
                .userId("userId")
                .seat(EntityMock.seatMock())
                .price(10000)
                .bookingTime(LocalDateTime.now())
                .status(TicketStatus.CANCELLED)
                .payment(EntityMock.paymentMock())
                .build();
        when(ticketService.getDetail(ticketId)).thenReturn(response);

        mockMvc.perform(get("/tickets/public/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(response.getId()))

                .andExpect(jsonPath("result.seat.id").value(response.getSeat().getId()))
                .andExpect(jsonPath("result.seat.eventId").value(response.getSeat().getEventId()))
                .andExpect(jsonPath("result.seat.seatNumber").value(response.getSeat().getSeatNumber()))
                .andExpect(jsonPath("result.seat.status").value(response.getSeat().getStatus().name()))

                .andExpect(jsonPath("result.bookingTime").value(response.getBookingTime().format(formatter)))
                .andExpect(jsonPath("result.status").value(response.getStatus().name()))

                .andExpect(jsonPath("result.payment.id").value(response.getPayment().getId()))
                .andExpect(jsonPath("result.payment.ticketId").value(response.getPayment().getTicketId()))
                .andExpect(jsonPath("result.payment.amount").value(response.getPayment().getAmount()))
                .andExpect(jsonPath("result.payment.status").value(response.getPayment().getStatus().name()));

    }

    @Test
    void booking_success() throws Exception {
        BookRequest request = BookRequest.builder()
                .userId("userId")
                .seatId(1)
                .build();
        String response = "urlVnp";
        var content = objectMapper.writeValueAsString(request);
        when(ticketService.booking(request)).thenReturn(response);

        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result").value(response));
    }

    @Test
    void booking_fail_blank() throws Exception {
        ErrorCode errorCode = ErrorCode.NOT_BLANK;
        BookRequest request = BookRequest.builder()
                .userId("")
                .seatId(1)
                .build();
        String response = "urlVnp";

        var content = objectMapper.writeValueAsString(request);

        when(ticketService.booking(request)).thenReturn(response);
        String finalMessage = errorCode.getMessage().replace("{field}", "userId");
        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(finalMessage));

    }

    @Test
    void booking_fail_null() throws Exception {
        ErrorCode errorCode = ErrorCode.NOT_NULL;
        BookRequest request = BookRequest.builder()
                .userId("userId")
                .build();
        String response = "urlVnp";

        var content = objectMapper.writeValueAsString(request);

        when(ticketService.booking(request)).thenReturn(response);
        String finalMessage = errorCode.getMessage().replace("{field}", "seatId");
        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(finalMessage));

    }
}
