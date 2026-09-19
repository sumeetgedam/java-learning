package com.learning.core.generics;

import java.util.ArrayList;
import java.util.List;

public class GenericsAndTypeSafety {

    public static <T> void printValue(T value) {
        System.out.println(value);
    }

    public static <T extends Number> double doubleValue(T number) {
        return number.doubleValue();
    }

    public static void printList(List<?> values) {
        for (Object value: values) {
            System.out.println(value);
        }
    }

    public static double sumNumbers(
            List<? extends Number> numbers
    ){
        double sum  = 0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }

    public static void addIntegers(
            List<? super Integer> values
    ){
        values.add(10);
        values.add(20);
    }

    static double sum(List<? extends Number> values) {
        // Reads values
        return 0;
    }

    static void addValues(List<? super Integer> values) {
        // Adds Integer values
    }

    public static void main(String[] args) {
        printValue("Java");
        printValue(100);
        printValue(true);

        doubleValue(10);
        doubleValue(10.5);
//        doubleValue("Java");

        printList(List.of("Java", "Spring"));
        printList(List.of(1, 2, 3));

        sumNumbers(List.of(1, 2, 3));
        sumNumbers(List.of(1.3, 4.5));

        List<Integer> integers = new ArrayList<>();
        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        addIntegers(integers);
        addIntegers(numbers);
        addIntegers(objects);


    }
}
