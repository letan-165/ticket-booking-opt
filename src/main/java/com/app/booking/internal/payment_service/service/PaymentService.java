package com.app.booking.internal.payment_service.service;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    TicketRepository ticketRepository;
    SeatRepository seatRepository;

    @Cacheable(value = "payments", keyGenerator  = "pageableKeyGenerator")
    public List<Payment> findAll(Pageable pageable){
        return paymentRepository.findAll(pageable).getContent();
    }

    @Cacheable(value = "payments", keyGenerator  = "pageableKeyGenerator")
    public List<Payment> findAllByOrganizerId(String organizerId,Pageable pageable){
        return paymentRepository.findAllByOrganizerId(organizerId,pageable).getContent();
    }

    @CacheEvict(value = "payments", allEntries = true)
    public Payment create(Integer ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new AppException(ErrorCode.TICKET_NO_EXISTS));

        Payment payment = Payment.builder()
                .ticketId(ticketId)
                .createdAt(LocalDateTime.now())
                .amount(ticket.getPrice())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @CacheEvict(value = {"payments", "tickets", "events"}, allEntries = true)
    public Payment pay(Integer paymentID,boolean isSuccess){
        Payment payment = paymentRepository.findById(paymentID)
                .orElseThrow(()->new AppException(ErrorCode.PAYMENT_NO_EXISTS));

        if(!payment.getStatus().equals(PaymentStatus.PENDING))
            throw new AppException(ErrorCode.PAYMENT_NO_PENDING);

        PaymentStatus paymentStatus = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        paymentStatus(payment.getTicketId(),isSuccess);
        payment.setStatus(paymentStatus);
        return paymentRepository.save(payment);
    }

    void paymentStatus(Integer ticketId, boolean isSuccess){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new AppException(ErrorCode.TICKET_NO_EXISTS));

        SeatStatus seatStatus = isSuccess ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;
        TicketStatus ticketStatus = isSuccess ? TicketStatus.CONFIRMED : TicketStatus.CANCELLED;

        Seat seat = seatRepository.findById(ticket.getSeatId())
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NO_EXISTS));

        seat.setStatus(seatStatus);
        seatRepository.save(seat);
        ticket.setStatus(ticketStatus);
        ticketRepository.save(ticket);
    }

}
