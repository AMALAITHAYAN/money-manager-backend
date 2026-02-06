package com.moneymanager.service;

import com.moneymanager.dto.transfer.CreateTransferRequest;
import com.moneymanager.dto.transaction.TransactionResponse;
import com.moneymanager.exception.BadRequestException;
import com.moneymanager.model.Account;
import com.moneymanager.model.Transaction;
import com.moneymanager.model.enums.TransactionType;
import com.moneymanager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransferService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public TransferService(AccountService accountService,
                           TransactionRepository transactionRepository,
                           TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public TransactionResponse create(String userId, CreateTransferRequest req) {
        if (req.getFromAccountId().equals(req.getToAccountId())) {
            throw new BadRequestException("fromAccountId and toAccountId must be different");
        }
        if (req.getAmount() <= 0) {
            throw new BadRequestException("Amount must be > 0");
        }

        Account from = accountService.requireOwnedAccount(userId, req.getFromAccountId());
        Account to = accountService.requireOwnedAccount(userId, req.getToAccountId());

        if (from.getBalance() < req.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        from.setBalance(from.getBalance() - req.getAmount());
        to.setBalance(to.getBalance() + req.getAmount());

        accountService.save(from);
        accountService.save(to);

        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setType(TransactionType.TRANSFER);
        t.setAmount(req.getAmount());
        t.setDescription(req.getNote() == null || req.getNote().isBlank() ? "Transfer" : req.getNote().trim());
        t.setCategory("Transfer");
        t.setDivision(null);
        t.setAccountId(null);
        t.setFromAccountId(from.getId());
        t.setToAccountId(to.getId());
        t.setOccurredAt(req.getOccurredAt());
        t.setCreatedAt(Instant.now());
        t.setUpdatedAt(Instant.now());

        t = transactionRepository.save(t);
        return transactionService.getById(userId, t.getId());
    }
}
