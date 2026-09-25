package com.learning.boot.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    @KafkaListener(
            topics = "orders",
            groupId = "payment-group"
    )
    public void consume(
            OrderCreatedEvent event
    ){
        System.out.println("Received order : " + event.orderId());
    }

    private void processOnce(
            OrderCreatedEvent event
    ) {
        System.out.println("Processing payment : " + event.total());
    }
}
