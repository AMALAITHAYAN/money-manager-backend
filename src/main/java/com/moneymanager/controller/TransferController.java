package com.moneymanager.controller;

import com.moneymanager.dto.transfer.CreateTransferRequest;
import com.moneymanager.dto.transaction.TransactionResponse;
import com.moneymanager.service.TransferService;
import com.moneymanager.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransactionResponse create(@Valid @RequestBody CreateTransferRequest req) {
        String userId = SecurityUtil.requireUserId();
        return transferService.create(userId, req);
    }
}
