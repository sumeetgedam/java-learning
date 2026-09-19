package com.learning.core.lambdas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaDemo {

    Runnable task = new Runnable() {
        @Override
        public void run() {
            System.out.println("Running");
        }
    };

    Runnable t = () -> System.out.println("Running");

    public static void main(String[] args) {
        Predicate<Integer> isEven = number -> number % 2 == 0;

        System.out.println(isEven.test(10));

        Consumer<String> printer = text -> System.out.println(text);

        printer.accept("Java");

        Function<String, Integer> length = text -> text.length();
        System.out.println(length.apply("Java"));

        Supplier<String> defaultName = () -> "Unkown";
        System.out.println(defaultName.get());

        List<String> names = new ArrayList<>();
        names.add("alex");
        names.add("jordan");
        names.forEach(name -> System.out.println(name));

        names.forEach(System.out::println);

        Function<String, Integer> len = String::length;
        Predicate<String> empty = String::isEmpty;

        names.removeIf(name -> name.length() < 6);
        names.forEach(System.out::println);

        String prefix = "User: ";
        Consumer<String> print = name -> System.out.println(prefix + name);

        String pre = "User:";
        pre = "Person:"; // Reassignment
        // Lambda cannot safely capture prefix now



    }


}
