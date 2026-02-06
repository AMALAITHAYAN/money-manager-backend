package com.moneymanager.dto.transaction;

import com.moneymanager.model.enums.Division;
import com.moneymanager.model.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class CreateTransactionRequest {

    @NotNull
    private TransactionType type;

    @Min(value = 1, message = "amount must be > 0")
    private double amount;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotNull
    private Division division;

    @NotBlank
    private String accountId;

    @NotNull
    private Instant occurredAt;

    public CreateTransactionRequest() {}

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
