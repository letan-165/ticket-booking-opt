package com.app.ticket_service.messaging.consumer;


import com.app.ticket_common_library.common.enums.TicketStatus;
import com.app.ticket_service.entity.Ticket;
import com.app.ticket_service.messaging.dto.CreateBookingMessaging;
import com.app.ticket_service.messaging.dto.LockSeatDQLMessaging;
import com.app.ticket_service.messaging.mq.BookingMQ;
import com.app.ticket_service.messaging.mq.LockSeatMQ;
import com.app.ticket_service.repository.client.PaymentClient;
import com.app.ticket_service.service.TicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingConsumer {
    PaymentClient paymentClient;
    TicketService ticketService;
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = BookingMQ.CREATE_BOOKING_QUEUE)
    public void create(CreateBookingMessaging consumer) {
        Ticket ticket = ticketService.save(Ticket.builder()
                .userId(consumer.getUserId())
                .seatId(consumer.getSeatId())
                .bookingTime(LocalDateTime.now())
                .price(consumer.getPrice())
                .status(TicketStatus.BOOKED)
                .build());

        rabbitTemplate.convertAndSend(LockSeatMQ.LOCK_SEAT_QUEUE, LockSeatDQLMessaging.builder()
                .ticketID(ticket.getId())
                .paymentID(consumer.getPaymentId())
                .build());

        paymentClient.update(consumer.getPaymentId(), ticket.getId());
    }
}
