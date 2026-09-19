package com.learning.core.basics;

public class VariablesAndTypes{

    public static void main(String[] args) {
        int age = 30;
        double salary = 75_000.50;
        boolean employed = true;
        char performanceGrade = 'A';

        System.out.println("Age: " + age);
        System.out.println("Salary: " + salary);
        System.out.println("Employed : " + employed);
        System.out.println("Grade: " + performanceGrade);

        int firstNumber = 10;
        int secondNumber = firstNumber;

        firstNumber = 20;

        System.out.println("First number : " + firstNumber);
        System.out.println("Second number : " + secondNumber);
    }
}