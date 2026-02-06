package com.moneymanager.dto.analytics;

public class ChartPoint {
    private String label;
    private double income;
    private double expense;

    public ChartPoint() {}

    public ChartPoint(String label, double income, double expense) {
        this.label = label;
        this.income = income;
        this.expense = expense;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
