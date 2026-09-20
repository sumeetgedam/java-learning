package com.learning.spring.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLoggingAspect {

    @AfterThrowing(
            pointcut = "execution(* com.learning.spring.aop.UserService.*(..))",
            throwing = "exception"
    )
    public void logException(Exception exception) {
        System.out.println("Method failed : " + exception.getMessage());
    }
}
