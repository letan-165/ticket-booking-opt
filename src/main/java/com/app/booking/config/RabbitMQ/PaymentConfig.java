package com.app.booking.config.RabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfig {
    public static final String PAYMENT_EXCHANGE = "Payment";
    public static final String PAYMENT_QUEUE = "payment";

    @Bean
    DirectExchange paymentExchange(){
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    Queue paymentQueue(){
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }

    @Bean
    Binding paymentBinding(Queue paymentQueue,DirectExchange paymentExchange){
        return BindingBuilder.bind(paymentExchange).to(paymentExchange).with(PAYMENT_QUEUE);
    }
}
