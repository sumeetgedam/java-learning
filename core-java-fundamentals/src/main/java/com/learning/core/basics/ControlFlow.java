package com.learning.core.basics;

public class ControlFlow {

    public static void main(String[] args) {
        int score = 82;

        if (score >= 90) {
            System.out.println("Excellent");
        } else if (score >= 70) {
            System.out.println("Good");
        } else if (score >= 50) {
            System.out.println("Needs improvement");
        } else {
            System.out.println("Fail");
        }

        int age = 20;
        String category = age >= 18 ? "Adult" : "Minor";

        System.out.println(category);

        int day = 2;
        switch (day){
            case 1:
                System.out.println("Monday");
                break;
            case 2:
                System.out.println("Tuesday");
                break;
            case 3:
                System.out.println("Wednesday");
                break;
            default:
                System.out.println("Unknown day");
        }

        String dayName = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            default -> "Unkown";
        };

        System.out.println(dayName);
    }
}