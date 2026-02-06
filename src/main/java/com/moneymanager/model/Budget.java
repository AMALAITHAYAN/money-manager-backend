package com.moneymanager.model;

import com.moneymanager.model.enums.Division;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Budget is stored per (userId, month, division, category)
 * month format: YYYY-MM
 */
@Document(collection = "budgets")
public class Budget {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String month; // YYYY-MM
    private Division division;
    private String category;

    private double limitAmount;

    private Instant createdAt;
    private Instant updatedAt;

    public Budget() {
    }

    public Budget(String userId, String month, Division division, String category, double limitAmount) {
        this.userId = userId;
        this.month = month;
        this.division = division;
        this.category = category;
        this.limitAmount = limitAmount;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
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
