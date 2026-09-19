package com.learning.spring;

import com.learning.spring.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(TestConfig.class)
public class TestConfigIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void shouldUseTestConfiguration() {
        assertNotNull(notificationService);
    }
}
