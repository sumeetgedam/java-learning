package com.learning.spring.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class LifecycleComponent {

    public LifecycleComponent() {
        System.out.println("1. Constructor");
    }

    @PostConstruct
    public void initialize() {
        System.out.println("2. PostConstruct");
    }

    public void execute() {
        System.out.println("3. Bean is ready");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("4. PreDestroy");
    }
}
