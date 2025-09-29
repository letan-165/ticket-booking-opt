package com.app.booking.internal.payment_service.service;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.keycloak_service.service.AuthService;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.entity.TransactionPay;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.repository.TransactionPayRepository;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.messaging.dto.PaymentMessaging;
import com.app.booking.messaging.mq.PaymentMQ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    TransactionPayRepository transactionPayRepository;
    TicketRepository ticketRepository;
    RabbitTemplate rabbitTemplate;
    AuthService authService;
    VNPayService vnPayService;

    @Cacheable(value = "payments", keyGenerator = "pageableKeyGenerator")
    public List<Payment> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable).getContent();
    }

    @Cacheable(value = "payments", keyGenerator = "pageableKeyGenerator")
    public List<Payment> findAllByOrganizerId(String organizerId, Pageable pageable) {
        authService.checkUserToken(organizerId);
        return paymentRepository.findAllByOrganizerId(organizerId, pageable).getContent();
    }

    @CacheEvict(value = "payments", allEntries = true)
    public Payment update(Integer paymentId, Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NO_EXISTS));

        Payment payment = Payment.builder()
                .id(paymentId)
                .ticketId(ticketId)
                .createdAt(LocalDateTime.now())
                .amount(ticket.getPrice())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    public TransactionPay paid(HttpServletRequest request) {
        Integer paymentId = Integer.valueOf(request.getParameter("vnp_TxnRef"));
        boolean isPaid = "00".equals(request.getParameter("vnp_TransactionStatus"));
        rabbitTemplate.convertAndSend(PaymentMQ.PAYMENT_QUEUE, PaymentMessaging.builder()
                .paymentId(paymentId)
                .paid(isPaid)
                .build());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        TransactionPay transactionPay = TransactionPay.builder()
                .id(request.getParameter("vnp_TransactionNo"))
                .txnRef(paymentId)
                .gatewayType("VNPAY")
                .amount(Integer.parseInt(request.getParameter("vnp_Amount")) / 100)
                .extraInfo(request.getParameter("vnp_OrderInfo"))
                .responseCode(request.getParameter("vnp_ResponseCode"))
                .bankCode(request.getParameter("vnp_BankCode"))
                .payDate(LocalDateTime.parse(request.getParameter("vnp_PayDate"), formatter))
                .build();

        return transactionPayRepository.save(transactionPay);
    }

    public String retryPay(Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NO_EXISTS));

        authService.checkUserToken(ticket.getUserId());
        
        Payment payment = Payment.builder()
                .ticketId(ticketId)
                .createdAt(LocalDateTime.now())
                .amount(ticket.getPrice())
                .status(PaymentStatus.PENDING)
                .build();

        Payment paymentRes = paymentRepository.save(payment);
        return vnPayService.create(paymentRes.getId(), paymentRes.getAmount(), "Thanh toán lại vé: " + ticket.getId());
    }

    @CacheEvict(value = {"payments", "tickets", "events", "seat"}, allEntries = true)
    public Payment updateStatus(Integer paymentID, boolean isSuccess) {
        Payment payment = paymentRepository.findById(paymentID)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NO_EXISTS));

        if (!payment.getStatus().equals(PaymentStatus.PENDING))
            throw new AppException(ErrorCode.PAYMENT_NO_PENDING);

        PaymentStatus paymentStatus = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        payment.setStatus(paymentStatus);
        return paymentRepository.save(payment);
    }


}
