package com.learning.spring.notification;

public class NotificationService {

    private final NotificationSender sender;

    public NotificationService(NotificationSender sender) {
        this.sender = sender;
    }
    public void notifyUser(String recipient, String message) {
        sender.send(recipient, message);
    }

}
