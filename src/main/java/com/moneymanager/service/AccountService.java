package com.moneymanager.service;

import com.moneymanager.dto.account.AccountResponse;
import com.moneymanager.dto.account.CreateAccountRequest;
import com.moneymanager.exception.BadRequestException;
import com.moneymanager.exception.NotFoundException;
import com.moneymanager.model.Account;
import com.moneymanager.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse create(String userId, CreateAccountRequest req) {
        String name = req.getName().trim();
        if (accountRepository.existsByUserIdAndNameIgnoreCase(userId, name)) {
            throw new BadRequestException("Account name already exists");
        }
        Account a = new Account(userId, name, req.getInitialBalance());
        a = accountRepository.save(a);
        return toResponse(a);
    }

    public List<AccountResponse> list(String userId) {
        return accountRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Account requireOwnedAccount(String userId, String accountId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    public void save(Account a) {
        a.setUpdatedAt(Instant.now());
        accountRepository.save(a);
    }

    private AccountResponse toResponse(Account a) {
        return new AccountResponse(a.getId(), a.getName(), a.getBalance(), a.getCreatedAt(), a.getUpdatedAt());
    }
}
