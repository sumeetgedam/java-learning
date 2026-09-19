package com.learning.core.generics;

public class Pair<K, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public static void main(String[] args) {
        Pair<String, Integer> employee = new Pair<>("Alex", 101);

        System.out.println(employee.getKey());
        System.out.println(employee.getValue());
    }
}
