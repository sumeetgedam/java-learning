package com.learning.spring.transaction;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<Long, Double> balances = new HashMap<>();

    public InMemoryAccountRepository() {
        balances.put(1L, 1_000.0);
        balances.put(2L, 500.0);
    }

    @Override
    public void withdraw(long accountId, double amount) {
        double current = balances.getOrDefault(accountId, 0.0);

        if(current < amount) {
            throw  new IllegalArgumentException("Insufficient funds");
        }

        balances.put(accountId, current - amount);

    }

    @Override
    public void deposit(long accountId, double amount) {
        double current = balances.getOrDefault(accountId, 0.0);

        balances.put(accountId, current + amount);
    }

    public double balance(long accountId) {
        return balances.getOrDefault(accountId, 0.0);
    }

}


