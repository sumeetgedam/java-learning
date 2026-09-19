package com.learning.spring.notification;

public interface NotificationSender {

    void send(String recipient, String message);
}
