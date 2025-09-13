package com.app.booking.messaging.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LockSeatMQ {
    public static final String LOCK_SEAT_EXCHANGE = "LockSeat";
    public static final String LOCK_SEAT_QUEUE = "lock-seat";

    public static final String LOCK_SEAT_DLQ_EXCHANGE = "LockSeatDLQ";
    public static final String LOCK_SEAT_QUEUE_DQL = "lock-seat-dlq";

    @Value("${vnp.expire}")
    private Integer expire;

    @Bean
    DirectExchange lockSeatExchange(){
        return new DirectExchange(LOCK_SEAT_EXCHANGE, true, false);
    }
    @Bean
    DirectExchange lockSeatDLQExchange(){
        return new DirectExchange(LOCK_SEAT_DLQ_EXCHANGE, true, false);
    }

    @Bean
    Queue lockSeatQueue(){
        return QueueBuilder
                .durable(LOCK_SEAT_QUEUE)
                .withArgument("x-message-ttl", expire*1000)
                .withArgument("x-dead-letter-exchange", LOCK_SEAT_DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", LOCK_SEAT_QUEUE_DQL)
                .build();
    }

    @Bean
    public Queue lockSeatQueueDLQ() {
        return QueueBuilder.durable(LOCK_SEAT_QUEUE_DQL).build();
    }

    @Bean
    public Binding dlxBinding(Queue lockSeatQueueDLQ, DirectExchange lockSeatDLQExchange) {
        return BindingBuilder.bind(lockSeatQueueDLQ).to(lockSeatDLQExchange).with(LOCK_SEAT_QUEUE_DQL);
    }

    @Bean
    public Binding lockSeatBinding(Queue lockSeatQueue, DirectExchange lockSeatExchange) {
        return BindingBuilder.bind(lockSeatQueue).to(lockSeatExchange).with(LockSeatMQ.LOCK_SEAT_QUEUE);
    }

}
