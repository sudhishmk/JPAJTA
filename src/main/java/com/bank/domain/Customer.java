package com.bank.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Cacheable // Enables Second-Level Caching for this entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // RELATIONAL MAPPING: One Customer -> Many Accounts
    // cascade = ALL means if we save a Customer, it auto-saves their Accounts
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    public Customer() {}

    public Customer(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    
    public List<Account> getAccounts() { return accounts; }
    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this); // Maintain the bidirectional link
    }
}
