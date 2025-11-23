package com.app.payment_service.messaging.consumer;


import com.app.payment_service.entity.Payment;
import com.app.payment_service.messaging.dto.PaymentMessaging;
import com.app.payment_service.messaging.mq.PaymentMQ;
import com.app.payment_service.repository.client.TicketClient;
import com.app.payment_service.service.PaymentService;
import com.app.ticket_common_library.common.exception.AppException;
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
    TicketClient ticketClient;

    @RabbitListener(queues = PaymentMQ.PAYMENT_QUEUE)
    public void payment(PaymentMessaging messaging) {
        try {
            Payment payment = paymentService.updateStatus(messaging.getPaymentId(), messaging.isPaid());
            ticketClient.updateStatus(payment.getTicketId(), messaging.isPaid());
        } catch (AppException e) {
            log.info("PaymentConsumer.payment{}", e.getMessage());
        }
    }

}
