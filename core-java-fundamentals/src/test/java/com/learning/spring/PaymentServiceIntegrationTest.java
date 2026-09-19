package com.learning.spring;

import com.learning.spring.profile.PaymentProcessor;
import com.learning.spring.profile.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentProcessor processor;

    @Test
    void shouldUseTestPaymentProcessor() {
        paymentService.pay(24.00);

        assertInstanceOf(TestPaymentProcessor.class, processor);
    }
}
