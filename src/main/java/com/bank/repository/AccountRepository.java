package com.bank.repository;

import com.bank.domain.Account;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped
public class AccountRepository {

    @Inject
    EntityManager em;

    /**
     * JPQL (Jakarta Persistence Query Language)
     * Pros: Highly readable, looks like SQL, but operates on Java Objects.
     * Cons: Prone to syntax errors inside the String that you only catch at runtime.
     */
    public List<Account> findHighValueAccountsJPQL(Double minBalance) {
        String jpql = "SELECT a FROM Account a WHERE a.balance >= :minBalance";
        return em.createQuery(jpql, Account.class)
                 .setParameter("minBalance", minBalance)
                 .getResultList();
    }

    /**
     * Criteria API
     * Pros: 100% Type-Safe. Great for building dynamic queries based on UI filters.
     * Cons: Very verbose and harder to read.
     */
    public List<Account> findHighValueAccountsCriteria(Double minBalance) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        
        // The "FROM" clause
        Root<Account> accountRoot = cq.from(Account.class);
        
        // The "SELECT ... WHERE ..." clause
        cq.select(accountRoot)
          .where(cb.greaterThanOrEqualTo(accountRoot.get("balance"), minBalance));

        return em.createQuery(cq).getResultList();
    }
}
