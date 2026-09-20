package com.learning.spring.jdbc;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JdbcDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(JdbcConfig.class);

        try{
            UserRepository repository = context.getBean(UserRepository.class);

            repository.save(
                    "Alex",
                    "alex@example.com"
            );

            repository.save(
                    "Jordan",
                    "jordan@example.com"
            );

            repository.findAll()
                    .forEach(System.out::println);
        }finally{
            context.close();
        }
    }
}
