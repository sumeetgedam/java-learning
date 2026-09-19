package com.learning.core.interfaces;

public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing card payment: " + amount);
    }

    public static void main(String[] args) {
        PaymentProcessor processor = new CardPaymentProcessor();

        processor.processPayment(100.0);
    }
}