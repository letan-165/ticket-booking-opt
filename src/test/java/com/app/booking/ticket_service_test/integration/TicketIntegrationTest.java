package com.app.booking.ticket_service_test.integration;

import com.app.booking.common.AbstractIntegrationTest;
import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.EventRepository;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.app.booking.messaging.mq.BookingMQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
class TicketIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    VNPayService vnPayService;

    @MockitoBean
    RabbitTemplate rabbitTemplate;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    EventRepository eventRepository;

    List<Ticket> tickets;
    Ticket ticket;
    Seat seat;
    User user;

    @BeforeEach
    void initData() {
        int init = 3;
        tickets = new ArrayList<>();
        List<Seat> seats = seatRepository.findAll(PageRequest.of(0, init)).getContent();
        user = userRepository.findAll(Pageable.ofSize(1)).getContent().get(0);
        for (int i = 0; i < init; i++) {
            seat = seats.get(i);
            tickets.add(ticketRepository.save(Ticket.builder()
                    .userId(user.getId())
                    .seatId(seat.getId())
                    .bookingTime(LocalDateTime.now())
                    .price(10000)
                    .status(TicketStatus.BOOKED)
                    .build()));
        }

        ticket = tickets.get(init - 1);
    }

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get("/tickets/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void getByUser_success() throws Exception {
        mockMvc.perform(get("/tickets/public/user/{userId}", ticket.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void getDetail_success() throws Exception {
        Payment payment = paymentRepository.save(Payment.builder()
                .ticketId(ticket.getId())
                .amount(ticket.getPrice())
                .status(PaymentStatus.PENDING)
                .build());


        mockMvc.perform(get("/tickets/public/{ticketId}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(ticket.getId()))

                .andExpect(jsonPath("result.seat.id").value(seat.getId()))
                .andExpect(jsonPath("result.seat.eventId").value(seat.getEventId()))
                .andExpect(jsonPath("result.seat.seatNumber").value(seat.getSeatNumber()))
                .andExpect(jsonPath("result.seat.status").value(seat.getStatus().name()))

                .andExpect(jsonPath("result.status").value(ticket.getStatus().name()))

                .andExpect(jsonPath("result.payment.id").value(payment.getId()))
                .andExpect(jsonPath("result.payment.ticketId").value(payment.getTicketId()))
                .andExpect(jsonPath("result.payment.amount").value(payment.getAmount()))
                .andExpect(jsonPath("result.payment.status").value(payment.getStatus().name()));
    }

    @Test
    void getDetail_fail_PAYMENT_NO_EXISTS() throws Exception {
        ErrorCode errorCode = ErrorCode.PAYMENT_NO_EXISTS;
        Ticket t = tickets.get(1);
        mockMvc.perform(get("/tickets/public/{ticketId}", t.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }

    @Test
    void booking_success() throws Exception {
        List<Seat> seats = seatRepository.findAll(PageRequest.of(1, 1)).getContent();

        BookRequest request = BookRequest.builder()
                .userId(ticket.getUserId())
                .seatId(seats.get(0).getId())
                .build();
        String response = "urlVnp";
        when(vnPayService.create(anyInt(), anyInt(), anyString())).thenReturn(response);

        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result").value(response));

        verify(rabbitTemplate).convertAndSend(
                ArgumentMatchers.<String>eq(BookingMQ.CREATE_BOOKING_QUEUE),
                ArgumentMatchers.<Object>any()
        );

    }

    @Test
    void booking_fail_USER_NO_EXISTS() throws Exception {
        BookRequest request = BookRequest.builder()
                .userId("fake")
                .seatId(seat.getId())
                .build();
        String response = "urlVnp";

        when(vnPayService.create(anyInt(), anyInt(), anyString())).thenReturn(response);

        ErrorCode errorCode = ErrorCode.USER_NO_EXISTS;
        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }

    @Test
    void booking_fail_SEAT_NO_EXISTS() throws Exception {
        BookRequest request = BookRequest.builder()
                .userId(user.getId())
                .seatId(999999999)
                .build();
        String response = "urlVnp";

        when(vnPayService.create(anyInt(), anyInt(), anyString())).thenReturn(response);

        ErrorCode errorCode = ErrorCode.SEAT_NO_EXISTS;
        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }

    @Test
    void booking_fail_TICKET_NO_AVAILABLE() throws Exception {
        seat.setStatus(SeatStatus.LOCKED);
        seatRepository.save(seat);

        BookRequest request = BookRequest.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();
        String response = "urlVnp";

        when(vnPayService.create(anyInt(), anyInt(), anyString())).thenReturn(response);

        ErrorCode errorCode = ErrorCode.TICKET_NO_AVAILABLE;
        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }

    @Test
    void booking_fail_PRICE_EVENT_INVALID() throws Exception {
        Event event = eventRepository.findById(seat.getEventId()).orElse(null);
        Assertions.assertNotNull(event);

        event.setPriceTicket(null);
        eventRepository.save(event);

        BookRequest request = BookRequest.builder()
                .userId(user.getId())
                .seatId(seat.getId())
                .build();
        String response = "urlVnp";

        when(vnPayService.create(anyInt(), anyInt(), anyString())).thenReturn(response);

        ErrorCode errorCode = ErrorCode.PRICE_EVENT_INVALID;
        mockMvc.perform(post("/tickets/public/book")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }
}
