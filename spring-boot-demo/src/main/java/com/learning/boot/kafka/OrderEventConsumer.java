package com.learning.boot.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics="orders",
            groupId="payment-group",
            concurrency = "3"
    )
    public void consume(
            OrderCreatedEvent event
    ) {
        System.out.println("Received order : " + event.orderId());

        processPayment(event);
    }

    private void processPayment(OrderCreatedEvent event) {
        System.out.println("Processing payment for : " + event.total());
    }
}
