package com.learning.boot.observability;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {

        boolean available  = checkPaymentGateway();

        if(available) {
            return Health.up()
                    .withDetail(
                            "provider",
                            "example-payments"
                    )
                    .build();
        }

        return Health.down()
                .withDetail(
                        "provider",
                        "example-payments"
                )
                .withDetail(
                        "reason",
                        "Gateway-unavailable"
                )
                .build();
    }

    private boolean checkPaymentGateway() {
        return true;
    }
}
