package com.learning.core.concurrency;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException{

        int workerCount = 3;
        CountDownLatch done = new CountDownLatch(workerCount);

        for(int i = 1; i <= workerCount; i++) {
            int workerId = i;

            new Thread(()->{
                try{
                    System.out.println("Worker " + workerId + " started");

                    Thread.sleep(500);

                    System.out.println("Worker " + workerId + " finished");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }finally {
                    done.countDown();
                }
            }).start();
        }

        done.await();
        System.out.println("All workers completed.");
    }
}
