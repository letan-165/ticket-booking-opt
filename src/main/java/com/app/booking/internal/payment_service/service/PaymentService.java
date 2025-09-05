package com.app.booking.internal.payment_service.service;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    TicketRepository ticketRepository;

    public Page<Payment> findAll(Pageable pageable){
        return paymentRepository.findAll(pageable);
    }

    public Page<Payment> findAllByOrganizerId(String organizerId,Pageable pageable){
        return paymentRepository.findAllByOrganizerId(organizerId,pageable);
    }

    public Payment create(Integer ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()->new AppException(ErrorCode.TICKET_NO_EXISTS));

        Payment payment = Payment.builder()
                .ticketId(ticketId)
                .createdAt(LocalDateTime.now())
                .amount(ticket.getPrice())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

}
