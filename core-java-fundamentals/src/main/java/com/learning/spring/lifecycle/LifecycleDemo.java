package com.learning.spring.lifecycle;

import com.learning.spring.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class LifecycleDemo {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            LifecycleComponent component = context.getBean(LifecycleComponent.class);
            component.execute();
            ScopeDemoComponent first =  context.getBean(ScopeDemoComponent.class);
            ScopeDemoComponent second = context.getBean(ScopeDemoComponent.class);

            System.out.println(first == second);
        }finally {
            context.close();
        }


    }
}
