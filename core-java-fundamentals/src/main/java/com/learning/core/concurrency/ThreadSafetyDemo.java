package com.learning.core.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafetyDemo {

    interface Counter{
        void increment();
        int getValue();
    }
    static class UnsafeCounter implements Counter {
        private int value;

        @Override
        public void increment() {
            value++;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
    static class SynchronizedCounter implements Counter {
        private int value;

        @Override
        public synchronized void increment() {
            value++;
        }

        public synchronized int getValue() {
            return value;
        }
    }
    static class AtomicCounter implements  Counter {
        private final AtomicInteger value = new AtomicInteger();

        public void increment() {
            value.incrementAndGet();
        }
        public int getValue() {
            return value.get();
        }
    }
    static class LockedCounter implements Counter {
        private int value;
        private final Lock lock = new ReentrantLock();

        public void increment() {
            lock.lock();
            try{
                value++;
            }finally {
                lock.unlock();
            }
        }
        public int getValue() {
            lock.lock();
            try{
                return value;
            }finally{
                lock.unlock();
            }
        }
    }
    private static void incrementMany(Counter counter) {
        for (int i = 0; i < 100_000; i++) {
            counter.increment();
        }
    }

    private static void runCounter(
            Counter counter,
            String name
    ) throws InterruptedException {
        Thread first = new Thread(()->incrementMany(counter));
        Thread second = new Thread(() -> incrementMany(counter));

        first.start();
        second.start();

        first.join();
        second.join();

        System.out.println(name + " result : " + counter.getValue());


    }



    public static void main(String[] args) throws InterruptedException {

        runCounter(
                new UnsafeCounter(),
                "Unsafe"
        );

        runCounter(
                new SynchronizedCounter(),
                "Synchronized"
        );

        runCounter(
                new AtomicCounter(),
                "Atomic"
        );

        runCounter(
                new LockedCounter(),
                "ReentrantLock"
        );

    }
}
