package com.learning.spring.config;

import com.learning.spring.notification.EmailNotificationSender;
import com.learning.spring.notification.NotificationSender;
import com.learning.spring.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NotificationSender notificationSender() {
        return new EmailNotificationSender();
    }

    @Bean
    public NotificationService notificationService(NotificationSender notificationSender ) {
        return new NotificationService(notificationSender);
    }
}
