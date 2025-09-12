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
import jakarta.servlet.http.HttpServletRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Payment update(Integer paymentId,Integer ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new AppException(ErrorCode.TICKET_NO_EXISTS));

        Payment payment = Payment.builder()
                .id(paymentId)
                .ticketId(ticketId)
                .createdAt(LocalDateTime.now())
                .amount(ticket.getPrice())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    public Payment paid(HttpServletRequest request){
        //TODO:  message ack TTL
        Integer paymentId = Integer.valueOf(request.getParameter("vnp_TxnRef"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new AppException(ErrorCode.PAYMENT_NO_EXISTS));
        if(!payment.getStatus().equals(PaymentStatus.PENDING))
            throw new AppException(ErrorCode.PAYMENT_NO_PENDING);

        boolean isPaid = "00".equals(request.getParameter("vnp_TransactionStatus"));

        if (isPaid){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            payment = payment.toBuilder()
                    .transactionId(request.getParameter("vnp_TransactionNo"))
                    .responseCode(request.getParameter("vnp_ResponseCode"))
                    .bankCode(request.getParameter("vnp_BankCode"))
                    .payDate(LocalDateTime.parse(request.getParameter("vnp_PayDate"),formatter))
                    .build();
        }
        //TODO: message update ticket
        payment.setStatus(isPaid ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    @CacheEvict(value = {"payments", "tickets", "events", "seat"}, allEntries = true)
    public void updateStatus(Integer paymentID,boolean isSuccess){
        Payment payment = paymentRepository.findById(paymentID)
                .orElseThrow(()->new AppException(ErrorCode.PAYMENT_NO_EXISTS));

        if(!payment.getStatus().equals(PaymentStatus.PENDING))
            throw new AppException(ErrorCode.PAYMENT_NO_PENDING);

        PaymentStatus paymentStatus = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
    }


}
