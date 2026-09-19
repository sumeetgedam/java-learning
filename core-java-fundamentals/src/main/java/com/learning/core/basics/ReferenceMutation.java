package com.learning.core.basics;

public class ReferenceMutation {

    static class Person {
        String name;
    }

    public static void main(String[] args) {
        Person firstPerson = new Person();
        firstPerson.name = "Alex";

        Person secondPerson = firstPerson;
        secondPerson.name = "Jordan";

        System.out.println("firstPerson = " + firstPerson.name);
        System.out.println("secondPerson = " + secondPerson.name);
    }
}