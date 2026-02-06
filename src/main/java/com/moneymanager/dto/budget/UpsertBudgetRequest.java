package com.moneymanager.dto.budget;

import com.moneymanager.model.enums.Division;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpsertBudgetRequest {

    // optional: if null, backend uses current month
    private String month; // yyyy-MM

    @NotNull
    private Division division;

    @NotBlank
    private String category;

    @Min(value = 0, message = "limitAmount must be >= 0")
    private double limitAmount;

    public UpsertBudgetRequest() {}

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
}
