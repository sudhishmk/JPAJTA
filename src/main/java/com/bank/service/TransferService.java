package com.bank.service;

import com.bank.domain.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TransferService {

    @Inject
    EntityManager em;

    // JTA intercepts this. Starts a transaction before, commits after (or rolls back on error).
    @Transactional
    public void transferMoney(Long fromId, Long toId, Double amount) {
        
        System.out.println(">>> Starting transfer of $" + amount + " from Account " + fromId + " to " + toId);

        Account fromAccount = em.find(Account.class, fromId);
        Account toAccount = em.find(Account.class, toId);

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Account(s) not found.");
        }

        if (fromAccount.getBalance() < amount) {
            System.out.println(">>> Throwing exception! JTA will roll back everything!");
            throw new IllegalStateException("Insufficient funds!");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        
        System.out.println(">>> Transfer logic complete. Waiting for JTA to commit...");
    }
}
