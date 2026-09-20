package com.learning.spring.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResultLoggingAspect {

    @AfterReturning(pointcut = "execution(* com.learning.spring.aop.UserService.find*(..))",
    returning = "result")
    public void logResult(Object result) {
        System.out.println("Returned : " + result);
    }
}
