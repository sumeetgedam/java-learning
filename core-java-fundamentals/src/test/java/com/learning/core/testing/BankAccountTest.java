package com.learning.core.testing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BankAccountTest {

    @Test
    void shouldIncreaseBalanceAfterDeposit() {
        BankAccount account = new BankAccount(100.0);

        account.deposit(50.0);

        assertEquals(150.0, account.getBalance());

    }

    @Test
    void shouldRejectNonPositiveDeposit() {
        BankAccount account = new BankAccount(100.0) ;

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );
    }

    @Test
    void shouldRejectNegativeOpeningBalance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BankAccount(-1.0)
        );
    }
}
