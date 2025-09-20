package com.app.booking.ticket_service_test.service;

import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.mapper.TicketMapper;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.internal.ticket_service.service.TicketService;
import com.app.booking.internal.user_service.service.UserService;
import com.app.booking.messaging.dto.CreateBookingMessaging;
import com.app.booking.messaging.mq.BookingMQ;
import com.app.booking.model_mock.EntityMock;
import com.app.booking.model_mock.RequestMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class TicketServiceTest {
    @Autowired
    @InjectMocks
    TicketService ticketService;

    @Mock PaymentRepository paymentRepository;
    @Mock TicketRepository ticketRepository;
    @Mock SeatRepository seatRepository;
    @Mock UserService userService;
    @Mock TicketMapper ticketMapper;
    @Mock VNPayService vnPayService;
    @Mock RabbitTemplate rabbitTemplate;


    @Test
    void booking_success(){
        Payment payment = EntityMock.paymentMock();
        BookRequest request = RequestMock.bockingMock();
        Seat seat = Seat.builder()
                .status(SeatStatus.AVAILABLE)
                .seatNumber("G1")
                .build();
        Integer price = 10000;
        String vnpUrl = "vpn url";

        when(seatRepository.findSeatForUpdate(request.getSeatId())).thenReturn(Optional.of(seat));
        when(seatRepository.findPriceBySeatId(request.getSeatId())).thenReturn(price);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(vnPayService.create(anyInt(),anyInt(),anyString())).thenReturn(vnpUrl);

        String response = ticketService.booking(request);

        verify(userService).userIsExist(eq(request.getUserId()));
        verify(seatRepository).save(eq(seat));
        verify(paymentRepository).save(any());
        verify(rabbitTemplate).convertAndSend(
                eq(BookingMQ.CREATE_BOOKING_QUEUE),
                any(CreateBookingMessaging.class));

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.LOCKED);
        assertThat(response).isEqualTo(vnpUrl);

    }

    @Test
    void booking_fail_SEAT_NO_EXISTS(){
        BookRequest request = RequestMock.bockingMock();

        when(seatRepository.findSeatForUpdate(request.getSeatId())).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class,
                ()-> ticketService.booking(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEAT_NO_EXISTS);

    }

    @Test
    void booking_fail_TICKET_NO_AVAILABLE(){
        BookRequest request = RequestMock.bockingMock();
        Seat seat = Seat.builder()
                .status(SeatStatus.BOOKED)
                .seatNumber("G1")
                .build();

        when(seatRepository.findSeatForUpdate(request.getSeatId())).thenReturn(Optional.of(seat));
        var exception = assertThrows(AppException.class,
                ()-> ticketService.booking(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TICKET_NO_AVAILABLE);
    }

    @Test
    void updateStatus_success_isPaid(){
        boolean isPaid = true;
        Ticket ticket = EntityMock.ticketMock();
        Seat seat = Seat.builder()
                .status(SeatStatus.LOCKED)
                .build();

        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(seatRepository.findById(ticket.getSeatId())).thenReturn(Optional.of(seat));

        ticketService.updateStatus(ticket.getId(), isPaid);

        var seatCaptor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(seatCaptor.capture());

        var ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketCaptor.capture());

        Seat seatCapture = seatCaptor.getValue();
        Ticket ticketCapture = ticketCaptor.getValue();

        assertThat(seatCapture.getStatus()).isEqualTo(SeatStatus.BOOKED);
        assertThat(ticketCapture.getStatus()).isEqualTo(TicketStatus.CONFIRMED);
    }

    @Test
    void updateStatus_success_noPaid(){
        boolean isPaid = false;
        Ticket ticket = EntityMock.ticketMock();
        Seat seat = Seat.builder()
                .status(SeatStatus.LOCKED)
                .build();

        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(seatRepository.findById(ticket.getSeatId())).thenReturn(Optional.of(seat));

        ticketService.updateStatus(ticket.getId(), isPaid);

        var seatCaptor = ArgumentCaptor.forClass(Seat.class);
        verify(seatRepository).save(seatCaptor.capture());

        var ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketCaptor.capture());

        Seat seatCapture = seatCaptor.getValue();
        Ticket ticketCapture = ticketCaptor.getValue();

        assertThat(seatCapture.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
        assertThat(ticketCapture.getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }

}
