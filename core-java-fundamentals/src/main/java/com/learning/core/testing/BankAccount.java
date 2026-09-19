package com.learning.core.testing;

public class BankAccount {

    private double balance;

    public BankAccount(double openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative"
            );
        }
        this.balance = openingBalance;
    }

    public void deposit(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit must be positive"
            );
        }
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }
}
