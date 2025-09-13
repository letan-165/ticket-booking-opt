package com.app.booking.messaging.consumer;

import com.app.booking.common.exception.AppException;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.ticket_service.service.TicketService;
import com.app.booking.messaging.dto.LockSeatDQLMessaging;
import com.app.booking.messaging.dto.PaymentMessaging;
import com.app.booking.messaging.mq.LockSeatMQ;
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
    PaymentService paymentService;
    TicketService ticketService;

    @RabbitListener(queues = LockSeatMQ.LOCK_SEAT_QUEUE_DQL)
    public void paymentFail(LockSeatDQLMessaging lockSeatDQLMessaging){
        try{
            ticketService.updateStatus(lockSeatDQLMessaging.getTicketID(), false);
            paymentService.updateStatus(lockSeatDQLMessaging.getPaymentID(), false);
        } catch (AppException e) {
            log.info("LockSeatConsumer.paymentFail{}",e.getMessage());
        }
    }

}
