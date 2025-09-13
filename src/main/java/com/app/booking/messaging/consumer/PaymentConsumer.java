package com.app.booking.messaging.consumer;

import com.app.booking.common.exception.AppException;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.ticket_service.service.TicketService;
import com.app.booking.messaging.dto.PaymentMessaging;
import com.app.booking.messaging.mq.PaymentMQ;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentConsumer {
    PaymentService paymentService;
    TicketService ticketService;

    @RabbitListener(queues = PaymentMQ.PAYMENT_QUEUE)
    public void payment(PaymentMessaging messaging){
        try {
            Payment payment = paymentService.updateStatus(messaging.getPaymentId(), messaging.isPaid());
            ticketService.updateStatus(payment.getTicketId(), messaging.isPaid());
        } catch (AppException e){
            log.info("PaymentConsumer.payment{}",e.getMessage());
        }
    }

}
