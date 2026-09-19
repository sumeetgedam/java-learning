package com.learning.core.testing;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorParameterizedTest {

    @ParameterizedTest
    @CsvSource({
            "1, 2, 3",
            "10, 20, 30",
            "-5, 5, 0"
    })
    void shouldAddMultiplePairs(
            int first, int second, int expected
    ) {
        Calculator calculator = new Calculator();

        assertEquals(
                expected,
                calculator.add(first, second)
        );
    }
}
