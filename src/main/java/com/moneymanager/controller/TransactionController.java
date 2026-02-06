package com.moneymanager.controller;

import com.moneymanager.dto.common.PageResponse;
import com.moneymanager.dto.transaction.CreateTransactionRequest;
import com.moneymanager.dto.transaction.TransactionResponse;
import com.moneymanager.dto.transaction.UpdateTransactionRequest;
import com.moneymanager.model.enums.Division;
import com.moneymanager.model.enums.TransactionType;
import com.moneymanager.service.TransactionService;
import com.moneymanager.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest req) {
        String userId = SecurityUtil.requireUserId();
        return transactionService.create(userId, req);
    }

    @GetMapping
    public PageResponse<TransactionResponse> list(
            @RequestParam(value = "type", required = false) TransactionType type,
            @RequestParam(value = "accountId", required = false) String accountId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "division", required = false) Division division,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        String userId = SecurityUtil.requireUserId();
        String divisionStr = division == null ? null : division.name();
        return transactionService.list(userId, type, accountId, category, divisionStr, start, end, page, size);
    }

    @GetMapping("/{id}")
    public TransactionResponse get(@PathVariable("id") String id) {
        String userId = SecurityUtil.requireUserId();
        return transactionService.getById(userId, id);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTransactionRequest req) {
        String userId = SecurityUtil.requireUserId();
        return transactionService.update(userId, id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        String userId = SecurityUtil.requireUserId();
        transactionService.delete(userId, id);
    }
}
