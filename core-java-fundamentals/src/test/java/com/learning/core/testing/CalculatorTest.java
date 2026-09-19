package com.learning.core.testing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    void shouldAddTwoNumbers() {
        //Arrange
        Calculator calculator = new Calculator();

        //Act
        int result = calculator.add(10, 20);

        //Assert
        assertEquals(30, result);
    }
}
