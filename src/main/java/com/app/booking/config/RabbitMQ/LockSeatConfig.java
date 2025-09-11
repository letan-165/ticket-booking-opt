package com.app.booking.config.RabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LockSeatConfig {
    public static final String LOCK_SEAT_EXCHANGE = "LockSeat";
    public static final String LOCK_SEAT_QUEUE = "lock - seat";

    public static final String LOCK_SEAT_DLQ_EXCHANGE = "LockSeat-DLQ";
    public static final String LOCK_SEAT_DQL = "lock - seat - dlq";

    @Bean
    DirectExchange lockSeatExchange(){
        return new DirectExchange(LOCK_SEAT_EXCHANGE, true, false);
    }

    @Bean
    Queue lockSeatQueue(){
        return QueueBuilder
                .durable(LOCK_SEAT_QUEUE)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", LOCK_SEAT_DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", LOCK_SEAT_DQL)
                .build();
    }

    @Bean
    public Queue lockSeatQueueDLQ() {
        return QueueBuilder.durable(LOCK_SEAT_DQL).build();
    }

    @Bean
    public Binding dlxBinding(Queue lockSeatQueueDLQ, DirectExchange lockSeatExchange) {
        return BindingBuilder.bind(lockSeatQueueDLQ).to(lockSeatExchange).with(LOCK_SEAT_DQL);
    }
}
