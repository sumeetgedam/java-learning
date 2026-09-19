package com.learning.spring.profile;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentProcessor processor;

    public PaymentService(PaymentProcessor processor) {
        this.processor= processor;
    }

    public void pay(double amount) {
        processor.process(amount);
    }
}
