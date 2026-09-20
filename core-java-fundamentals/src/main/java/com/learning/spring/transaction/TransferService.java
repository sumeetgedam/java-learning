package com.learning.spring.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(
            long sourceAccountId, long targetAccountId, double amount
    ) {
        accountRepository.withdraw(sourceAccountId, amount);
        accountRepository.deposit(targetAccountId, amount);
    }

}
