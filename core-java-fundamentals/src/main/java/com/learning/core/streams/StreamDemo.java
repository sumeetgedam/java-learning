package com.learning.core.streams;

import com.learning.core.collections.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class StreamDemo {

    public static void main(String[] args) {

        List<String> names = List.of("Alex", "Jordan", "Taylor");

        names.stream().forEach(System.out::println);

        List<String> longNames = names.stream()
                .filter(name -> name.length() > 5)
                .toList();

        System.out.println(longNames);

        List<String> uppercaseNames = names.stream()
                .map(String::toUpperCase)
                .toList();
        System.out.println(uppercaseNames);

        List<String> result = names.stream()
                .filter(name -> name.length() > 4)
                .map(String::toUpperCase)
                .sorted()
                .toList();
        System.out.println(result);

        List<Integer> sorted = List.of(30, 10, 20)
                .stream()
                .sorted()
                .toList();
        System.out.println(sorted);
        List<Integer> descending = List.of(40, 10, 30, 20)
                .stream()
                .sorted(Comparator.reverseOrder())
                .toList();
        System.out.println(descending);

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1, "Alex"));
        employees.add(new Employee(2, "Jordan"));
        employees.stream()
                .sorted(Comparator.comparing(Employee::getName))
                .toList();

        List<Integer> values = List.of(1, 2, 2, 3, 4, 4, 5);
        List<Integer> valueResult = values.stream()
                .distinct()
                .skip(1)
                .limit(3)
                .toList();
        System.out.println(valueResult);

        Set<String> uniqueNames = names.stream()
                .collect(Collectors.toSet());
        List<String> nameList = names.stream()
                .collect(Collectors.toList());
        List<String> mutableNames = names.stream()
                .collect(Collectors.toCollection(ArrayList::new));

        int sum = List.of(1, 2, 3, 4, 5)
                .stream()
                .reduce(0, Integer::sum);
        System.out.println(sum);

        int product = List.of(1, 2, 3, 4)
                .stream()
                .reduce(1, (first, second) -> first * second);
        System.out.println(product);

        long count = names.stream()
                .filter(name -> name.length() > 5)
                .count();
        System.out.println(count);

        boolean anyLongName = names.stream()
                .anyMatch(name -> name.length() > 5);
        System.out.println(anyLongName);

        boolean allNonEmpty = names.stream()
                .allMatch(name -> !name.isEmpty());
        System.out.println(allNonEmpty);

        boolean noneBlank = names.stream()
                .noneMatch(String::isBlank);
        System.out.println(noneBlank);

        Optional<String> firstLongName = names.stream()
                .filter(name -> name.length() > 5)
                .findFirst();
        String name = firstLongName.orElse("Not Found");

        List<List<String>> groups = List.of(
                List.of("Java", "Spring"),
                List.of("Kafka", "Docker")
        );

        List<String> technologies = groups.stream()
                .flatMap(List::stream)
                .toList();
        System.out.println(technologies);

    }
}
