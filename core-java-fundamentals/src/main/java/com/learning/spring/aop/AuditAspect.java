package com.learning.spring.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    @Before("@annotation(com.learning.spring.aop.Audited")
    public void audit() {
        System.out.println("Audited method invoked");
    }
}
