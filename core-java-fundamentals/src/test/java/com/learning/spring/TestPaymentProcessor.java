package com.learning.spring;

import com.learning.spring.profile.PaymentProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestPaymentProcessor implements PaymentProcessor {

    private double lastAmount;

    @Override
    public void process(double amount) {
        lastAmount = amount;
    }

    public double getLastAmount() {
        return lastAmount;
    }
}
