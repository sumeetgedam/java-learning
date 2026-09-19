package com.learning.core.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> queue =
                new LinkedBlockingQueue<>(2);

        Thread producer = new Thread(() -> {
            try{
                queue.put("message-1");
                queue.put("message-2");
                queue.put("message-3");
            }catch(InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(()->{
            try {
                for (int i = 0; i < 3; i++) {
                    String message = queue.take();
                    System.out.println("Consumed : " + message);
                }
            }catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

    }
}
