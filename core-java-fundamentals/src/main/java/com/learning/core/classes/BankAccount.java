package com.learning.core.classes;

public class BankAccount {

    private final String accountNumber;
    private double balance;

    public BankAccount(String accountNumber, double intialBalance) {
        this.accountNumber = accountNumber;
        this.balance = intialBalance;
    }

    public BankAccount(String accountNumber) {
        this(accountNumber, 0.0);
    }

    public void deposit(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }

        balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setBalance(double balance) {
        if(balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public void printBalance() {
        System.out.println("balance : " + balance);
    }
}