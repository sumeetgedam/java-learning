package com.learning.core.testing;

public class Calculator {

    public int add(int first, int second) {
        return first + second;
    }

    public int divide(int first, int second) {
        if(second == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }

        return first / second;
    }
}
