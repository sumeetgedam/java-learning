package com.learning.core.basics;

public class JavaRuntimeInfo {

    public static void main(String[] args) {
        System.out.println("Java version: " +
                System.getProperty("java.version"));

        System.out.println("Java vendor: " +
                System.getProperty("java.vendor"));

        System.out.println("Operating system: " +
                System.getProperty("os.name"));

        System.out.println("JVM name: " +
                System.getProperty("java.vm.name"));

        System.out.println("Working directory: " +
                System.getProperty("user.dir"));

        System.out.println("Available processors: " +
                Runtime.getRuntime().availableProcessors());

        System.out.println("Maximum memory: " +
                Runtime.getRuntime().maxMemory());
    }
}