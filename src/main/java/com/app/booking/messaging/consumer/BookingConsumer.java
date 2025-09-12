package com.app.booking.messaging.consumer;

import com.app.booking.common.enums.TicketStatus;
import com.app.booking.config.RabbitMQ.BookingConfig;
import com.app.booking.config.RabbitMQ.LockSeatConfig;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.service.TicketService;
import com.app.booking.messaging.dto.CreateBookingConsumer;
import com.app.booking.messaging.dto.LockSeatDQL;
import com.rabbitmq.client.Channel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingConsumer {
    PaymentService paymentService;
    TicketService ticketService;
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = BookingConfig.CREATE_BOOKING_QUEUE)
    public void create(CreateBookingConsumer consumer) throws InterruptedException {
        Ticket ticket = ticketService.save(Ticket.builder()
                .userId(consumer.getUserId())
                .seatId(consumer.getSeatId())
                .bookingTime(LocalDateTime.now())
                .price(consumer.getPrice())
                .status(TicketStatus.BOOKED)
                .build());

        rabbitTemplate.convertAndSend(LockSeatConfig.LOCK_SEAT_QUEUE, LockSeatDQL.builder()
                .ticketID(ticket.getId())
                        .paymentID(consumer.getPaymentId())
                .build());

        paymentService.update(consumer.getPaymentId(),ticket.getId());
    }

    @RabbitListener(queues = LockSeatConfig.LOCK_SEAT_QUEUE_DQL)
    public void paymentFail(LockSeatDQL lockSeatDQL){
        log.info("paymentFail");
        ticketService.updateStatus(lockSeatDQL.getTicketID(), false);
        paymentService.updateStatus(lockSeatDQL.getPaymentID(), false);
    }



}
