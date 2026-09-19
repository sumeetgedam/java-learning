package com.learning.core.concurrency;


public class DeadlockDemo {

    public static void main(String[] args) throws InterruptedException {

        Object lockOne = new Object();
        Object lockTwo = new Object();

        Thread first = new Thread(() -> {
            synchronized (lockOne) {
                sleep(100);

                synchronized (lockTwo) {
                    System.out.println("First completed");
                }
            }
        }, "first-thread");

        Thread second = new Thread(()->{
            synchronized (lockTwo){
                sleep(100);

                synchronized (lockOne) {
                    System.out.println("Second completed");
                }
            }
        }, "second-thread");

        first.start();
        second.start();

        first.join();
        second.join();

    }

    private static void sleep(long milliseconds) {
        try{
            Thread.sleep(milliseconds);
        }catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

}
