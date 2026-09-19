package com.learning.spring;

import com.learning.spring.config.AppConfig;
import com.learning.spring.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
public class NotificationServiceIntegrationTest {

    @Autowired

    private NotificationService notificationService;

    @Test
    void shouldLoadNotificationService() {
        assertNotNull(notificationService);
    }
}
