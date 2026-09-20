package com.learning.spring.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopDemo {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class);

        try {
            UserService service = context.getBean(UserService.class);

            String user = service.findUser(101);

            System.out.println(user);
        }finally {
            context.close();
        }

    }
}
