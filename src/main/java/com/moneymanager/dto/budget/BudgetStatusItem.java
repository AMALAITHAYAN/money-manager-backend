package com.moneymanager.dto.budget;

import com.moneymanager.model.enums.Division;

public class BudgetStatusItem {
    private String month;
    private Division division;
    private String category;

    private double limitAmount;
    private double spent;
    private double remaining;
    private double percentUsed;

    private String status; // OK / WARN / OVER

    public BudgetStatusItem() {}

    public BudgetStatusItem(String month, Division division, String category, double limitAmount, double spent) {
        this.month = month;
        this.division = division;
        this.category = category;
        this.limitAmount = limitAmount;
        this.spent = spent;
        this.remaining = limitAmount - spent;
        this.percentUsed = limitAmount <= 0 ? 0 : (spent / limitAmount) * 100.0;
        if (limitAmount <= 0) {
            this.status = "OK";
        } else if (spent > limitAmount) {
            this.status = "OVER";
        } else if (this.percentUsed >= 80.0) {
            this.status = "WARN";
        } else {
            this.status = "OK";
        }
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }

    public double getSpent() { return spent; }
    public void setSpent(double spent) { this.spent = spent; }

    public double getRemaining() { return remaining; }
    public void setRemaining(double remaining) { this.remaining = remaining; }

    public double getPercentUsed() { return percentUsed; }
    public void setPercentUsed(double percentUsed) { this.percentUsed = percentUsed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
