package com.learning.core.basics;

public class Methods {

    static class Person {
        String name;
    }

    static void changeName(Person person) {
        person.name = "Jordan";
    }

    static void replacePerson(Person person) {
        person = new Person();
        person.name = "Zoe";
    }

    static void printGreeting() {
        System.out.println("Hello, Java");
    }

    static int add(int first, int second) {
        return first + second;
    }

    static boolean isAdult(int age) {
        return age >= 18;
    }

    static double calculateAverage(int first, int second, int third) {
        return (first + second + third) / 3.0;
    }

    static void changeNumber(int number) {
        number = 100;
    }

    // Method overloading
    static int multiply(int a, int b) {
        return a * b;
    }

    static int multiply(int a, int b, int c) {
        return a * b * c;
    }

    // Invalid overload
//    static double multiply(int a, int b) {
//        return a * b;
//    }


    public static void main(String[] args) {
        printGreeting();
        
        int sum = add(10, 20);
        System.out.println("sum = " + sum);
        
        boolean valid = isAdult(29);
        System.out.println("valid = " + valid);
        
        double average = calculateAverage(80, 90, 70);
        System.out.println("average = " + average);
        
        int value = 10;
        changeNumber(value);
        System.out.println("value = " + value);

        Person person = new Person();
        person.name = "Alex";
        changeName(person);
        System.out.println(person.name);

        replacePerson(person);
        System.out.println(person.name);

        System.out.println(multiply(2, 3));
        System.out.println(multiply(2, 3, 4));

    }
    

    

    
}