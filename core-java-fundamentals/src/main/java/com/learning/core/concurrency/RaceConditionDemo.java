package com.learning.core.concurrency;

public class RaceConditionDemo {

    private static int counter = 0;

    public static void main(String[] args)
        throws InterruptedException {

        Thread first = new Thread(()->{
            for(int i = 0;i < 100_000; i++) {
                counter++;
            }
        });

        Thread second = new Thread(()->{
            for(int i = 0; i < 100_000; i++) {
                counter++;
            }
        });
        first.start();
        second.start();

        first.join();
        second.join();

        System.out.println("Expected : 200000");
        System.out.println("counter = " + counter);
    }
}
