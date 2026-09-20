package com.learning.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.learning.spring.aop.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println(
                "Calling : " + joinPoint.getSignature().getName()
        );
    }

    @After(
            "execution(* com.learning.spring.aop.UserService.*(..))"
    )
    public void logCompletion() {
        System.out.println("Method completed");
    }
}
