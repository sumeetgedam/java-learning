package com.learning.spring.notification;

import org.springframework.stereotype.Component;

@Component
public class ConsoleNotificationSender
        implements  NotificationSender {

    @Override
    public void send(String recipient, String message) {
        System.out.println("Message to " + recipient + " : " + message);
    }
}
