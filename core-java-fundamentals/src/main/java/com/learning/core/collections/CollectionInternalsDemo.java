package com.learning.core.collections;

import java.util.*;

public class CollectionInternalsDemo {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Alex");
        names.add("Jordan");
        names.add("Taylor");

        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Java", 1);
        hashMap.put("Spring", 2);

        Map<String, Integer> linkedMap = new LinkedHashMap<>();
        linkedMap.put("first", 1);
        linkedMap.put("second", 2);

        Map<Integer, String> sortedMap = new TreeMap<>();
        sortedMap.put(30, "C");
        sortedMap.put(10, "A");
        sortedMap.put(20, "B");

        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(30);
        priorityQueue.offer(10);
        priorityQueue.offer(20);

        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("first");
        deque.addLast("last");

        System.out.println("hashMap = " + hashMap);
        System.out.println("linkedMap = " + linkedMap);
        System.out.println("sortedMap = " + sortedMap);
        System.out.println("priorityQueue = " + priorityQueue.poll());
        System.out.println("deque.removeFirst() = " + deque.removeFirst());
    }
}
