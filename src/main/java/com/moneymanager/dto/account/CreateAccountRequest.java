package com.moneymanager.dto.account;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {

    @NotBlank
    private String name;

    @Min(value = 0, message = "initialBalance must be >= 0")
    private double initialBalance;

    public CreateAccountRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }
}
