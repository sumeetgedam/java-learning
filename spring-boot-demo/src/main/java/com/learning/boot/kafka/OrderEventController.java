package com.learning.boot.kafka;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/events/orders")
public class OrderEventController {

    private final OrderEventProducer producer;

    public OrderEventController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<Void> publish(
            @PathVariable long orderId,
            @RequestParam long customerId,
            @RequestParam BigDecimal total
    ) {
        producer.publish(
                new OrderCreatedEvent(
                        orderId,
                        customerId,
                        total,
                        Instant.now()
                )
        );
        return ResponseEntity.accepted().build();
    }
}
