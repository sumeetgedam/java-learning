package com.learning.core.basics;

public class References {

    public static void main(String[] args) {
        String firstName = "Alex";
        String anotherName = firstName;

        System.out.println(firstName == anotherName);

        String nameFromInput = new String("Alex");
        System.out.println(firstName == nameFromInput);
        System.out.println(firstName.equals(nameFromInput));
    }
}