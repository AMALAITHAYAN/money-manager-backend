package com.moneymanager.dto.report;

public class CategorySummaryItem {
    private String category;
    private double income;
    private double expense;

    public CategorySummaryItem() {}

    public CategorySummaryItem(String category, double income, double expense) {
        this.category = category;
        this.income = income;
        this.expense = expense;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }
}
