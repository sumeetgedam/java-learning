package com.learning.spring.config;

import com.learning.spring.notification.EmailNotificationSender;
import com.learning.spring.notification.NotificationSender;
import com.learning.spring.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.learning.spring")
public class AppConfig {

}
