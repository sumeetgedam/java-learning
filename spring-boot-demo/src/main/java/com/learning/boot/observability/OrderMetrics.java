package com.learning.boot.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {

    private final Counter ordersCreated;

    public OrderMetrics(MeterRegistry meterRegistry) {
        this.ordersCreated = Counter.builder("orders.created")
                .description("Number of created orders")
                .register(meterRegistry);
    }

    public void recordOrderCreated() {
        ordersCreated.increment();
    }

}
