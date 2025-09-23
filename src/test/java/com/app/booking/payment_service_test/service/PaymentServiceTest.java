package com.app.booking.payment_service_test.service;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.EntityMock;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.entity.TransactionPay;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.repository.TransactionPayRepository;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.messaging.dto.PaymentMessaging;
import com.app.booking.messaging.mq.PaymentMQ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PaymentServiceTest {
    @InjectMocks
    PaymentService paymentService;

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    TransactionPayRepository transactionPayRepository;
    @Mock
    TicketRepository ticketRepository;
    @Mock
    RabbitTemplate rabbitTemplate;
    @Mock
    VNPayService vnPayService;

    Ticket ticket;
    Payment payment;

    Integer ticketId = 1;
    Integer paymentId = 2;

    @BeforeEach
    void initData() {
        ticket = EntityMock.ticketMock();
        payment = EntityMock.paymentMock();
    }

    @Test
    void update_success() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment paymentUpdate = paymentService.update(paymentId, ticketId);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        Payment captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(paymentId);
        assertThat(captured.getTicketId()).isEqualTo(ticketId);
        assertThat(captured.getAmount()).isEqualTo(ticket.getPrice());
        assertThat(captured.getStatus()).isEqualTo(PaymentStatus.PENDING);

        assertThat(payment).isEqualTo(paymentUpdate);
    }

    @Test
    void update_fail_TICKET_NO_EXISTS() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                () -> paymentService.update(paymentId, ticketId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TICKET_NO_EXISTS);
    }

    @Test
    void paid_success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String id = "123";
        String transactionStatus = "00";
        String transactionNo = "txn123";
        String amountRaw = "20000";
        String orderInfo = "order-info";
        String responseCode = "00";
        String bankCode = "VCB";
        String payDateRaw = "20250920140000";

        when(request.getParameter("vnp_TxnRef")).thenReturn(id);
        when(request.getParameter("vnp_TransactionStatus")).thenReturn(transactionStatus);
        when(request.getParameter("vnp_TransactionNo")).thenReturn(transactionNo);
        when(request.getParameter("vnp_Amount")).thenReturn(amountRaw);
        when(request.getParameter("vnp_OrderInfo")).thenReturn(orderInfo);
        when(request.getParameter("vnp_ResponseCode")).thenReturn(responseCode);
        when(request.getParameter("vnp_BankCode")).thenReturn(bankCode);
        when(request.getParameter("vnp_PayDate")).thenReturn(payDateRaw);

        TransactionPay expected = TransactionPay.builder()
                .id(transactionNo)
                .txnRef(Integer.valueOf(id))
                .gatewayType("VNPAY")
                .amount(Integer.parseInt(amountRaw) / 100)
                .extraInfo(orderInfo)
                .responseCode(responseCode)
                .bankCode(bankCode)
                .payDate(LocalDateTime.now())
                .build();

        when(transactionPayRepository.save(any(TransactionPay.class))).thenReturn(expected);

        TransactionPay response = paymentService.paid(request);
        verify(rabbitTemplate).convertAndSend(eq(PaymentMQ.PAYMENT_QUEUE), any(PaymentMessaging.class));

        ArgumentCaptor<TransactionPay> captor = ArgumentCaptor.forClass(TransactionPay.class);
        verify(transactionPayRepository).save(captor.capture());

        TransactionPay captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(transactionNo);
        assertThat(captured.getTxnRef()).isEqualTo(Integer.parseInt(id));
        assertThat(captured.getGatewayType()).isEqualTo("VNPAY");
        assertThat(captured.getAmount()).isEqualTo(Integer.parseInt(amountRaw) / 100);
        assertThat(captured.getExtraInfo()).isEqualTo(orderInfo);
        assertThat(captured.getResponseCode()).isEqualTo(responseCode);
        assertThat(captured.getBankCode()).isEqualTo(bankCode);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void retryPay_success() {
        Ticket ticketMock = EntityMock.ticketMock();
        Payment paymentMock = EntityMock.paymentMock();
        String result = "vnp url";

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticketMock));
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentMock);
        when(vnPayService.create(paymentMock.getId(), paymentMock.getAmount(), "Thanh toán lại vé: " + ticketMock.getId())).thenReturn(result);

        String response = paymentService.retryPay(ticketId);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        Payment captured = captor.getValue();
        assertThat(captured.getTicketId()).isEqualTo(ticketId);
        assertThat(captured.getAmount()).isEqualTo(ticketMock.getPrice());
        assertThat(captured.getStatus()).isEqualTo(PaymentStatus.PENDING);

        assertThat(response).isEqualTo(result);
    }

    @Test
    void retryPay_fail_TICKET_NO_EXISTS() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                () -> paymentService.retryPay(ticketId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TICKET_NO_EXISTS);
    }

    @Test
    void updateStatus_success_isSuccess() {
        boolean isSuccess = true;
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentService.updateStatus(paymentId, isSuccess);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment captured = captor.getValue();

        assertThat(captured.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void updateStatus_success_noSuccess() {
        boolean isSuccess = false;
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentService.updateStatus(paymentId, isSuccess);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment captured = captor.getValue();

        assertThat(captured.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void updateStatus_fail_PAYMENT_NO_EXISTS() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                () -> paymentService.updateStatus(paymentId, true));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NO_EXISTS);
    }

    @Test
    void updateStatus_fail_PAYMENT_NO_PENDING() {
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        var exception = assertThrows(AppException.class,
                () -> paymentService.updateStatus(paymentId, true));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NO_PENDING);
    }
}
