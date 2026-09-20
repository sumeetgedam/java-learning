package com.learning.spring.transaction;

public interface AccountRepository {

    void withdraw(long accountId, double amount);

    void deposit(long accountId, double amount);
}
