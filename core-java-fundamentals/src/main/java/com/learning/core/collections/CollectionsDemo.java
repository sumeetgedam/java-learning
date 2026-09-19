package com.learning.core.collections;

import java.util.*;

public class CollectionsDemo {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();

        names.add("Alex");
        names.add("Jordan");
        names.add("Alex");

        System.out.println(names);
        System.out.println(names.get(0));
        System.out.println(names.size());

        names.set(1, "Taylor");
        names.remove("Alex");
        names.remove(0);
        System.out.println(names.contains("Jordan"));
        System.out.println(names.isEmpty());
        names.clear();

        names = List.of("Alex", "Jordan", "Tylor"); // creates immutable list
        // names.add("Joe"); // UnsupportedOperationException
        for (String name: names) {
            System.out.println(name);
        }

        Set<String> skills = new HashSet<>();
        skills.add("Java");
        skills.add("Spring");
        skills.add("Java");

        System.out.println(skills);

        Set<Integer> numbers = new TreeSet<>();
        numbers.add(30);
        numbers.add(10);
        numbers.add(20);
        System.out.println(numbers);

        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alex", 90);
        scores.put("Jordan", 85);
        scores.put("Alex", 95);
        System.out.println("scores.get(\"Alex\") = " + scores.get("Alex"));

        System.out.println(scores.containsKey("Alex"));
        System.out.println(scores.containsValue(85));
        System.out.println(scores.get("Jordan"));
        System.out.println(scores.remove("Jordan"));
        System.out.println(scores.size());

        // int score = scores.get("Taylor"); // Can cause NullPointerException during unboxing
        // prefer
        int score = scores.getOrDefault("Taylor", 0);
        System.out.println(score);

        for (String name: scores.keySet()) {
            System.out.println(name);
        }
        for (Integer score_1: scores.values()) {
            System.out.println(score_1);
        }

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        Map<Integer, Employee> employeeMap = new HashMap<>();
        employeeMap.put(1, new Employee(1, "Alex"));
        employeeMap.put(2, new Employee(2, "Jordan"));
        System.out.println(employeeMap.get(1).getId() + " " + employeeMap.get(1).getName());

        Employee first = new Employee(3, "Zoe");
        Employee second = new Employee(3, "Zoe");
        Set<Employee> employeeSet = new HashSet<>();
        employeeSet.add(first);
        employeeSet.add(second);
        System.out.println(employeeSet.size());



    }
}