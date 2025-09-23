package com.app.booking.messaging.consumer;

import com.app.booking.common.enums.TicketStatus;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.service.TicketService;
import com.app.booking.messaging.dto.CreateBookingMessaging;
import com.app.booking.messaging.dto.LockSeatDQLMessaging;
import com.app.booking.messaging.mq.BookingMQ;
import com.app.booking.messaging.mq.LockSeatMQ;
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
    PaymentService paymentService;
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

        paymentService.update(consumer.getPaymentId(), ticket.getId());
    }
}
