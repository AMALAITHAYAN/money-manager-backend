package com.moneymanager.controller;

import com.moneymanager.dto.budget.BudgetResponse;
import com.moneymanager.dto.budget.BudgetStatusItem;
import com.moneymanager.dto.budget.UpsertBudgetRequest;
import com.moneymanager.model.enums.Division;
import com.moneymanager.service.BudgetService;
import com.moneymanager.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public BudgetResponse upsert(@Valid @RequestBody UpsertBudgetRequest req) {
        String userId = SecurityUtil.requireUserId();
        return budgetService.upsert(userId, req);
    }

    @GetMapping
    public List<BudgetResponse> list(
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "division", required = false) Division division
    ) {
        String userId = SecurityUtil.requireUserId();
        return budgetService.list(userId, month, division);
    }

    @GetMapping("/status")
    public List<BudgetStatusItem> status(
            @RequestParam(value = "month", required = false) String month,
            @RequestParam("division") Division division
    ) {
        String userId = SecurityUtil.requireUserId();
        return budgetService.status(userId, month, division);
    }
}
