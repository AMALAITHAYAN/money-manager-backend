package com.moneymanager.dto.budget;

import com.moneymanager.model.enums.Division;

import java.time.Instant;

public class BudgetResponse {
    private String id;
    private String month;
    private Division division;
    private String category;
    private double limitAmount;
    private Instant createdAt;
    private Instant updatedAt;

    public BudgetResponse() {}

    public BudgetResponse(String id, String month, Division division, String category, double limitAmount,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.month = month;
        this.division = division;
        this.category = category;
        this.limitAmount = limitAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
