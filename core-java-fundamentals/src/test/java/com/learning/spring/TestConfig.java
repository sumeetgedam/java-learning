package com.learning.spring;

import com.learning.spring.notification.NotificationSender;
import com.learning.spring.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    NotificationSender notificationSender() {
        return (recipient, message) -> System.out.println(
                "Test message : " + message
        );
    }

    @Bean
    NotificationService notificationService(
            NotificationSender sender
    ){
        return new NotificationService(sender);
    }
}
