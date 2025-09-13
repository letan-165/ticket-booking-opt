package com.app.booking.messaging.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BookingMQ {
    public static final String CREATE_BOOKING_EXCHANGE = "CreateBooking";
    public static final String CREATE_BOOKING_QUEUE = "create-booking";

    @Bean
    DirectExchange createBookingExchange(){
        return new DirectExchange(CREATE_BOOKING_EXCHANGE, true, false);
    }

    @Bean
    Queue createBookingQueue(){
        return QueueBuilder
                .durable(CREATE_BOOKING_QUEUE)
                .build();
    }

    @Bean
    Binding createBookingBinding(DirectExchange createBookingExchange, Queue createBookingQueue){
        return BindingBuilder.bind(createBookingQueue).to(createBookingExchange).with(CREATE_BOOKING_QUEUE);
    }


}
