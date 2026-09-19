package com.learning.core.exceptions;

public class SimpleBankAccount {

    private double balance;

    public SimpleBankAccount(double balance) {
        this.balance = balance;
    }

    public void withdraw(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Withdraw must be positive");
        }

        if(amount > balance) {
            throw new InsufficientFundsException("Insufficient account balance");
        }

        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }

    public static void main(String[] args) {
        SimpleBankAccount simpleBankAccount = new SimpleBankAccount(100.0);
        simpleBankAccount.withdraw(20.0);
        simpleBankAccount.getBalance();
        simpleBankAccount.withdraw(100.0);
    }
}