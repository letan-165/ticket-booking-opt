package com.app.booking.payment_service_test.integration;

import com.app.booking.common.AbstractIntegrationTest;
import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.EventRepository;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.repository.TransactionPayRepository;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.app.booking.messaging.mq.PaymentMQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Transactional
class PaymentIntegrationTest extends AbstractIntegrationTest {
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

    @Autowired
    TransactionPayRepository transactionPayRepository;


    Payment payment;
    Event event;
    Ticket ticket;
    @Value("${vnp.feBackUrl}")
    String feBackUrl;

    @BeforeEach
    void initData() {
        int init = 3;
        event = eventRepository.findAll(Pageable.ofSize(1)).getContent().get(0);
        Seat seat = seatRepository.findAll(Pageable.ofSize(1)).getContent().get(0);
        ticket = ticketRepository.save(Ticket.builder()
                .userId(event.getOrganizerId())
                .seatId(seat.getId())
                .bookingTime(LocalDateTime.now())
                .price(10000)
                .status(TicketStatus.BOOKED)
                .build());

        for (int i = 0; i < init; i++) {
            payment = paymentRepository.save(Payment.builder()
                    .ticketId(ticket.getId())
                    .createdAt(LocalDateTime.now())
                    .amount(ticket.getPrice())
                    .status(PaymentStatus.PENDING)
                    .build());
        }
    }

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get("/payments/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void findAllByOrganizerId_success() throws Exception {
        mockMvc.perform(get("/payments/public/user/{userID}", event.getOrganizerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void paid_success() throws Exception {
        Integer paymentId = payment.getId();
        String transactionStatus = "00";
        String transactionNo = "txn123";
        String amountRaw = "20000";
        String orderInfo = "order-info";
        String responseCode = "00";
        String bankCode = "VCB";
        String payDateRaw = "20250920140000";

        mockMvc.perform(get("/payments/vnpay/return")
                        .param("vnp_TxnRef", String.valueOf(paymentId))
                        .param("vnp_TransactionStatus", transactionStatus)
                        .param("vnp_TransactionNo", transactionNo)
                        .param("vnp_Amount", amountRaw)
                        .param("vnp_OrderInfo", orderInfo)
                        .param("vnp_ResponseCode", responseCode)
                        .param("vnp_BankCode", bankCode)
                        .param("vnp_PayDate", payDateRaw))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(feBackUrl + "?paymentId=" + paymentId + "&status=" + true));
        
        verify(rabbitTemplate).convertAndSend(
                ArgumentMatchers.<String>eq(PaymentMQ.PAYMENT_QUEUE),
                ArgumentMatchers.<Object>any()
        );

        assertTrue(transactionPayRepository.existsById(transactionNo));
    }

    @Test
    void retry_success() throws Exception {
        String response = "urlVnp";
        when(vnPayService.create(anyInt(), anyInt(), anyString())).thenReturn(response);
        long countBefore = paymentRepository.count();

        mockMvc.perform(post("/payments/public/retry/{ticketId}", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result").value(response));
        long countAfter = paymentRepository.count();

        assertThat(countAfter).isEqualTo(countBefore + 1);
    }


}
