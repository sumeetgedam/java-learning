package com.learning.core.jvm;

import java.util.ArrayList;
import java.util.List;

public class GcDemo {

    public static void main(String[] args) throws InterruptedException {

        List<byte[]> temporaryObjects = new ArrayList<>();

        for(int i = 0; i < 1_000; i++) {
            temporaryObjects.add(new byte[1024 * 100]);

            if(i % 100 == 0) {
                printMemory(i);
                Thread.sleep(50);
            }

            if(temporaryObjects.size() > 20) {
                temporaryObjects.clear();
            }
        }
    }

    private static void printMemory(int iteration) {
        Runtime runtime = Runtime.getRuntime();

        long used = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Iteration = " + iteration +
                ", usedBytes = " + used +
                ", totalBytes = " + runtime.totalMemory() +
                ", maxBytes = " + runtime.maxMemory());
    }
}
