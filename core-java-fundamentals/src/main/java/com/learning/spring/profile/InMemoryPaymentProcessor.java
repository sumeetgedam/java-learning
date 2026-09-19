package com.learning.spring.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class InMemoryPaymentProcessor implements PaymentProcessor {

    @Override
    public void process(double amount) {
        System.out.println("Simulated payment : " + amount);
    }
}
