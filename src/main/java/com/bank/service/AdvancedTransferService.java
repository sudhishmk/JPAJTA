package com.bank.service;

import com.bank.domain.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

@ApplicationScoped
public class AdvancedTransferService {

    @Inject
    EntityManager em;

    // Inject the actual JTA Engine directly!
    @Inject
    UserTransaction userTransaction; 

    /**
     * Notice there is NO @Transactional annotation here.
     * We are manually controlling the JTA boundaries.
     */
    public void transferWithManualJTA(Long fromId, Long toId, Double amount) {
        try {
            // 1. Manually start the global transaction
            userTransaction.begin();

            Account fromAccount = em.find(Account.class, fromId);
            Account toAccount = em.find(Account.class, toId);

            if (fromAccount.getBalance() < amount) {
                // By throwing an exception, we skip the commit block and hit the catch block
                throw new IllegalStateException("Insufficient funds!");
            }

            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            // 2. Manually commit the transaction to the database
            userTransaction.commit();
            System.out.println("BMT Transfer Committed!");

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            try {
                // 3. Manually trigger the JTA Rollback
                userTransaction.rollback();
                System.out.println("BMT Transfer Rolled Back!");
            } catch (SystemException se) {
                se.printStackTrace();
            }
            throw new RuntimeException("Transfer Failed", e);
        }
    }
}
