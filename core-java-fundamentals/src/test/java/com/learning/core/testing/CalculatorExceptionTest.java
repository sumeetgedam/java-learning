package com.learning.core.testing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorExceptionTest {

    @Test
    void shouldRejectDivisionByZero() {
        Calculator calculator = new Calculator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(10, 0)
        );

        assertEquals(
                "Divisor cannot be zero",
                exception.getMessage()
        );
    }
}
