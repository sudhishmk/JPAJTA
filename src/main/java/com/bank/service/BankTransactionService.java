package com.bank.service;

import com.bank.domain.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;

@ApplicationScoped
public class BankTransactionService {

    @Inject
    EntityManager em;

    // The manual JTA Engine
    @Inject
    UserTransaction userTransaction;

    /**
     * APPROACH 1: Container-Managed Transaction (CMT)
     * The framework starts the transaction before the method, and commits/rolls back after.
     * Best Practice: Use this 95% of the time in modern apps!
     */
    @Transactional
    public void transferCMT(Long fromId, Long toId, Double amount) {
        // Explicitly using em.find() to retrieve managed entities
        Account fromAccount = null;
        Account toAccount = null;
        fromAccount = em.find(Account.class, fromId);
        toAccount = em.find(Account.class, toId);

        /*for(int i = 0; i < 10; i++) {
            System.out.println("CMT Iteration: " + i);
            fromAccount = em.find(Account.class, fromId);
            toAccount = em.find(Account.class, toId);
        }*/

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Account(s) not found.");
        }

        if (fromAccount.getBalance() < amount) {
            // Throwing a RuntimeException automatically triggers a JTA Rollback in CMT
            throw new IllegalStateException("CMT: Insufficient funds!");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
    }

    /**
     * APPROACH 2: Bean-Managed Transaction (BMT)
     * We manually command the JTA engine to begin, commit, and rollback.
     * Best Practice: Use this only for complex batch jobs or fine-grained boundary control.
     */
    public void transferBMT(Long fromId, Long toId, Double amount) throws Exception {
        try {
            // 1. Manually Start
            userTransaction.begin();

            // Using the exact same em.find() logic
            Account fromAccount = em.find(Account.class, fromId);
            Account toAccount = em.find(Account.class, toId);

            if (fromAccount == null || toAccount == null) {
                throw new IllegalArgumentException("Account(s) not found.");
            }

            if (fromAccount.getBalance() < amount) {
                // We still throw the exception to break the flow
                throw new IllegalStateException("BMT: Insufficient funds!");
            }

            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            // 2. Manually Commit
            userTransaction.commit();

        } catch (Exception e) {
            // 3. Manually Rollback
            if (userTransaction.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE || 
                userTransaction.getStatus() == jakarta.transaction.Status.STATUS_MARKED_ROLLBACK) {
                userTransaction.rollback();
            }
            throw e; // Re-throw so the API knows it failed
        }
    }
}
