package com.learning.core.concurrency;

public class ThreadBasicsDemo {

    public static void main(String[] args) throws InterruptedException {

        Thread worker = new Thread(()-> {
            System.out.println("Worker started: " + Thread.currentThread().getName());


            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Worker finished");

        }, "worker-1");

        worker.start();

        System.out.println("After start: " + worker.getState());

        worker.join();

        System.out.println("After" +
                "join : " + worker.getState());

        Thread thread = new Thread(()->{
            System.out.println("thread");
        });
        thread.run();
        System.out.println("Thread name: " + thread.getName() + "Thread state: " + thread.getState());
        thread.join();
        System.out.println("Thread name: " + thread.getName() + "Thread state : " + thread.getState());


    }

}
