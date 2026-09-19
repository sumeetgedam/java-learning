package com.learning.core.generics;

public class Box<T> {

    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static void main(String[] args) {
        Box<String> textBox = new Box<>("Java");
        String text = textBox.getValue();

        Box<Integer> numberBox = new Box<>(100);
        Integer number = numberBox.getValue();

        System.out.println(text + " " + number);
    }
}
