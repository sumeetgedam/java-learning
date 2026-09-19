package com.learning.core.design;

public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message
    ){
        System.out.println("Email to " + recipient + ": " + message);
    }
}
