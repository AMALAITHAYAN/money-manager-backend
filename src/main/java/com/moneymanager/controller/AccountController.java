package com.moneymanager.controller;

import com.moneymanager.dto.account.AccountResponse;
import com.moneymanager.dto.account.CreateAccountRequest;
import com.moneymanager.service.AccountService;
import com.moneymanager.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        String userId = SecurityUtil.requireUserId();
        return accountService.create(userId, req);
    }

    @GetMapping
    public List<AccountResponse> list() {
        String userId = SecurityUtil.requireUserId();
        return accountService.list(userId);
    }
}
