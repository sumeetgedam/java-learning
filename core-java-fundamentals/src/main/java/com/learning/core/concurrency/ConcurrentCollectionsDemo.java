package com.learning.core.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCollectionsDemo {

    public static void main(String[] args) throws InterruptedException {

        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();

        Thread first = new Thread(()->{
            for(int i = 0; i < 100_000; i++) {
                counts.merge("Java", 1, Integer::sum);
            }
        });

        Thread second = new Thread(()->{
            for(int i = 0; i < 100_000; i++) {
                counts.merge("Java", 1, Integer::sum);
            }
        });

        first.start();
        second.start();

        first.join();
        second.join();

        for(Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }
}
