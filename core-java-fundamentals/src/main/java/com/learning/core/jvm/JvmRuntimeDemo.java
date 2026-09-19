package com.learning.core.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class JvmRuntimeDemo {

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();

        System.out.println("Available processors : " + runtime.availableProcessors());

        System.out.println("Max heap bytes : " + runtime.maxMemory());

        System.out.println("Total heap bytes : " + runtime.totalMemory());

        System.out.println("Free heap bytes : " + runtime.freeMemory());

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        System.out.println("Heap Usage : " + heapUsage);
        System.out.println("Non-heap usage : " + nonHeapUsage);

        System.out.println("Class loader : " + JvmRuntimeDemo.class.getClassLoader());
    }
}
