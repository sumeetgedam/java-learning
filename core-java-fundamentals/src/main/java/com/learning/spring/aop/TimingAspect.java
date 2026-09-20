package com.learning.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimingAspect {

    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();

        try{
            return joinPoint.proceed();
        }finally {
            long duration = System.nanoTime() - start;

            System.out.println(joinPoint.getSignature().getName() + " took " + duration + "ns");
        }
    }
}
