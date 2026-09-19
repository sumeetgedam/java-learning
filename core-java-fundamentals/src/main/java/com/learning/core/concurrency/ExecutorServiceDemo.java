package com.learning.core.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            List<Callable<Integer>> tasks =
                    List.of(
                            ()->calculateSquare(2),
                            ()->calculateSquare(3),
                            ()->calculateSquare(4)
                    );

            List<Future<Integer>> futures = new ArrayList<>();
            for(Callable<Integer> task : tasks) {
                futures.add(executor.submit(task));
            }

            for(Future<Integer> future : futures) {
                try{
                    System.out.println("Result : " + future.get());
                }catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    System.out.println("Main thread interrupted");
                    break;
                } catch (ExecutionException e) {
                    System.out.println("Task failed: " + e.getCause());
                }
            }
        }finally {
            executor.shutdown();
        }
    }

    private static int calculateSquare(int value) throws InterruptedException {
        Thread.sleep(500);
        return value * value;
    }
}
