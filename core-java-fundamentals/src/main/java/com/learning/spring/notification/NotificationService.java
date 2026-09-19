package com.learning.spring.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationSender sender;

    public NotificationService(NotificationSender sender) {
        this.sender = sender;
    }
    public void notifyUser(String recipient, String message) {
        sender.send(recipient, message);
    }

}
