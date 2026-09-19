package com.learning.core.basics;

import java.util.Objects;
import java.util.Arrays;

public class ArraysAndStrings {

    public static void main(String[] args) {
        int[] numbers = {20, 10, 40, 30};

        System.out.println(numbers[0]);
        System.out.println(numbers[3]);

        System.out.println(numbers.length);

        int[] values = new int[3];
        values[0] = 10;
        values[1] = 20;
        values[2] = 30;
        // values[3] = 40; // ArrayIndexOutOfBoundsException

        for(int index = 0; index < numbers.length; index++) {
            System.out.println(numbers[index]);
        }

        for (int number : numbers) {
            System.out.println("number = " + number);
        }

        Arrays.sort(numbers);
        System.out.println(Arrays.toString(numbers));
        System.out.println(Arrays.binarySearch(numbers, 30));
        System.out.println(Arrays.equals(numbers, new int[]{10, 20, 30, 40}));


        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6}
        };
        System.out.println(matrix[0][1]);
        System.out.println(matrix[1][2]);

        for(int[] row: matrix) {
            for(int value : row) {
                System.out.println(value + " ");
            }
            System.out.println();
        }

        String message = "Hello Java";
        String text = " Java Programming ";

        System.out.println(text.length());
        System.out.println(text.trim());
        System.out.println(text.toUpperCase());
        System.out.println(text.toLowerCase());
        System.out.println(text.contains("Java"));
        System.out.println(text.startsWith(" Java"));
        System.out.println(text.substring(2, 6));

        text.toUpperCase();
        System.out.println(text);

        String upperText = text.toUpperCase();
        System.out.println(upperText);

        String first = new String("Java");
        String second = new String("Java");

        System.out.println(first == second);
        System.out.println(first.equals(second));

        // null safety, even if one or both are null
        System.out.println(Objects.equals(first, second));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 5 ; i++) {
            builder.append(i);
            if(i < 5) {
                builder.append(", ");
            }
        }

        String result = builder.toString();
        System.out.println(result);
        builder.append(" Java");
        builder.insert(0, "Learn ");
        System.out.println(builder.toString());
        builder.reverse();
        System.out.println(builder.toString());
        builder.delete(0, 6);
        System.out.println(builder.toString());

    }
}