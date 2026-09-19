package com.learning.core.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {

    public static void main(String[] args) {
        CompletableFuture<String> user =
                CompletableFuture.supplyAsync(()->"Alex");

        CompletableFuture<String> result =
                user.thenApply(String::toUpperCase)
                        .thenApply(name -> "Hello, " + name)
                        .exceptionally(exception ->
                                "Fallback greeting");

        System.out.println(result.join());
    }
}
