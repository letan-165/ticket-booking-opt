package com.app.ticket_service.messaging.consumer;

import com.app.ticket_common_library.common.exception.AppException;
import com.app.ticket_service.messaging.dto.LockSeatDQLMessaging;
import com.app.ticket_service.messaging.mq.LockSeatMQ;
import com.app.ticket_service.repository.client.PaymentClient;
import com.app.ticket_service.service.TicketService;
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
public class LockSeatConsumer {
    PaymentClient paymentClient;
    TicketService ticketService;

    @RabbitListener(queues = LockSeatMQ.LOCK_SEAT_QUEUE_DQL)
    public void paymentFail(LockSeatDQLMessaging lockSeatDQLMessaging) {
        try {
            paymentClient.updateStatus(lockSeatDQLMessaging.getPaymentID(), false);
            ticketService.updateStatus(lockSeatDQLMessaging.getTicketID(), false);
        } catch (AppException e) {
            log.info("LockSeatConsumer.paymentFail{}", e.getMessage());
        }
    }

}
