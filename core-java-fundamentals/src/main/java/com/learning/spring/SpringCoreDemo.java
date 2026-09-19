package com.learning.spring;

import com.learning.spring.config.AppConfig;
import com.learning.spring.notification.NotificationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringCoreDemo {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        NotificationService service = context.getBean(NotificationService.class);

        service.notifyUser(
                "alex@exampe.com",
                "Spring IoC is Working"
        );
    }
}
