package com.learning.boot.kafka;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        long orderId,
        long customerId,
        BigDecimal total,
        Instant occurredAt
) {
}
