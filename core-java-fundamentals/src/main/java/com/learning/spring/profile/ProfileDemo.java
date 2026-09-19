package com.learning.spring.profile;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ProfileDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("prod");

        context.scan("com.learning.spring");
        context.refresh();

        PaymentService service = context.getBean(PaymentService.class);

        service.pay(30.00);
        context.close();
    }
}
