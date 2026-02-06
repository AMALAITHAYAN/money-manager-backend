package com.moneymanager.dto.transaction;

import com.moneymanager.model.enums.Division;
import jakarta.validation.constraints.Min;

import java.time.Instant;

public class UpdateTransactionRequest {

    @Min(value = 1, message = "amount must be > 0")
    private Double amount;

    private String description;
    private String category;
    private Division division;

    private String accountId;

    private Instant occurredAt;

    public UpdateTransactionRequest() {}

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
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
