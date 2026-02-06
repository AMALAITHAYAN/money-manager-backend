package com.moneymanager.dto.account;

import java.time.Instant;

public class AccountResponse {
    private String id;
    private String name;
    private double balance;
    private Instant createdAt;
    private Instant updatedAt;

    public AccountResponse() {}

    public AccountResponse(String id, String name, double balance, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
