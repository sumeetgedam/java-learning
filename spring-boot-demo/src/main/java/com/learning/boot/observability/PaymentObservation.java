package com.learning.boot.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

@Component
public class PaymentObservation {

    private final ObservationRegistry registry;

    public PaymentObservation(ObservationRegistry registry) {
        this.registry = registry;
    }

    public void charge(
            Runnable operation
    ) {
        Observation observation = Observation.createNotStarted(
                "payment.charge",
                registry
        )
                .lowCardinalityKeyValue(
                        "provider",
                        "example"
                )
                .highCardinalityKeyValue(
                        "paymentReference",
                        "internal-reference"
                ).start();

        try(Observation.Scope ignored = observation.openScope()) {
            operation.run();
            observation.stop();
        }catch(RuntimeException exception) {
            observation.error(exception);
            observation.stop();
            throw exception;
        }
    }
}
