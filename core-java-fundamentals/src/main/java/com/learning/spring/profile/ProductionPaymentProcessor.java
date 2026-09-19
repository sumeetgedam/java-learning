package com.learning.spring.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductionPaymentProcessor implements PaymentProcessor{

    @Override
    public void process(double amount) {
        System.out.println("Real payment processing : " + amount);
    }
}
