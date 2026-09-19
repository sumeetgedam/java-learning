package com.learning.core.annotations;

public class ReflectionDemo {

    public static void main(String[] args) {
        Class<AnnotatedService> type = AnnotatedService.class;
        
        if(type.isAnnotationPresent(InspectMe.class)) {
            InspectMe annotation = type.getAnnotation(InspectMe.class);
            System.out.println("annotation.value() = " + annotation.value());
        }
    }
}
