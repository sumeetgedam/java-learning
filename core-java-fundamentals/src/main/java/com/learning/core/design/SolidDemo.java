package com.learning.core.design;

public class SolidDemo {

    public static void main(String[] args) {
        NotificationSender sender = new EmailNotificationSender();

        NotificationService service = new NotificationService(sender);

        service.notifyUser("alex@example.com", "Learning SOLID");
    }
}
