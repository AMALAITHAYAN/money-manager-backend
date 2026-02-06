package com.moneymanager.service;

import com.moneymanager.dto.common.PageResponse;
import com.moneymanager.dto.transaction.CreateTransactionRequest;
import com.moneymanager.dto.transaction.TransactionResponse;
import com.moneymanager.dto.transaction.UpdateTransactionRequest;
import com.moneymanager.exception.BadRequestException;
import com.moneymanager.exception.ForbiddenException;
import com.moneymanager.exception.NotFoundException;
import com.moneymanager.model.Account;
import com.moneymanager.model.Transaction;
import com.moneymanager.model.enums.TransactionType;
import com.moneymanager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final MongoTemplate mongoTemplate;

    private final int editWindowHours;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountService accountService,
            MongoTemplate mongoTemplate,
            @Value("${app.security.edit-window-hours:12}") int editWindowHours
    ) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.mongoTemplate = mongoTemplate;
        this.editWindowHours = editWindowHours;
    }

    public TransactionResponse create(String userId, CreateTransactionRequest req) {
        if (req.getType() == TransactionType.TRANSFER) {
            throw new BadRequestException("Use /api/transfers for transfers");
        }
        Account account = accountService.requireOwnedAccount(userId, req.getAccountId());

        double amount = req.getAmount();
        applyToBalance(account, req.getType(), amount);

        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setType(req.getType());
        t.setAmount(amount);
        t.setDescription(req.getDescription().trim());
        t.setCategory(req.getCategory().trim());
        t.setDivision(req.getDivision());
        t.setAccountId(account.getId());
        t.setOccurredAt(req.getOccurredAt());
        t.setCreatedAt(Instant.now());
        t.setUpdatedAt(Instant.now());

        t = transactionRepository.save(t);
        return toResponse(t);
    }

    public PageResponse<TransactionResponse> list(
            String userId,
            TransactionType type,
            String accountId,
            String category,
            String division,
            Instant start,
            Instant end,
            int page,
            int size
    ) {
        Criteria c = Criteria.where("userId").is(userId);

        if (type != null) {
            c = c.and("type").is(type);
        }
        if (accountId != null && !accountId.isBlank()) {
            c = c.and("accountId").is(accountId);
        }
        if (category != null && !category.isBlank()) {
            c = c.and("category").regex("^" + java.util.regex.Pattern.quote(category.trim()) + "$", "i");
        }
        if (division != null && !division.isBlank()) {
            c = c.and("division").is(division.trim().toUpperCase());
        }
        if (start != null && end != null) {
            c = c.and("occurredAt").gte(start).lt(end);
        } else if (start != null) {
            c = c.and("occurredAt").gte(start);
        } else if (end != null) {
            c = c.and("occurredAt").lt(end);
        }

        Query q = new Query(c);
        long total = mongoTemplate.count(q, Transaction.class);

        q.with(Sort.by(Sort.Direction.DESC, "occurredAt"));
        q.skip((long) page * size).limit(size);

        List<Transaction> items = mongoTemplate.find(q, Transaction.class);

        List<TransactionResponse> mapped = items.stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(mapped, total, page, size);
    }

    public TransactionResponse getById(String userId, String id) {
        Transaction t = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));
        return toResponse(t);
    }

    public TransactionResponse update(String userId, String id, UpdateTransactionRequest req) {
        Transaction t = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (t.getType() == TransactionType.TRANSFER) {
            throw new ForbiddenException("Transfer transactions cannot be edited");
        }

        enforceEditWindow(t);

        String oldAccountId = t.getAccountId();
        double oldAmount = t.getAmount();

        String newAccountId = (req.getAccountId() != null && !req.getAccountId().isBlank())
                ? req.getAccountId()
                : oldAccountId;

        double newAmount = req.getAmount() != null ? req.getAmount() : oldAmount;

        // Update balances if amount/account changed
        if (!newAccountId.equals(oldAccountId)) {
            Account oldAccount = accountService.requireOwnedAccount(userId, oldAccountId);
            Account newAccount = accountService.requireOwnedAccount(userId, newAccountId);

            // revert old impact on old account
            revertFromBalance(oldAccount, t.getType(), oldAmount);

            // apply new impact on new account (with balance validation)
            if (t.getType() == TransactionType.INCOME) {
                newAccount.setBalance(newAccount.getBalance() + newAmount);
            } else if (t.getType() == TransactionType.EXPENSE) {
                if (newAccount.getBalance() < newAmount) {
                    throw new BadRequestException("Insufficient balance for this update");
                }
                newAccount.setBalance(newAccount.getBalance() - newAmount);
            }

            accountService.save(oldAccount);
            accountService.save(newAccount);
            t.setAccountId(newAccountId);
        } else if (Double.compare(newAmount, oldAmount) != 0) {
            Account account = accountService.requireOwnedAccount(userId, oldAccountId);
            applyDelta(account, t.getType(), oldAmount, newAmount);
            accountService.save(account);
        }

        if (req.getAmount() != null) t.setAmount(newAmount);
        if (req.getDescription() != null) t.setDescription(req.getDescription().trim());
        if (req.getCategory() != null) t.setCategory(req.getCategory().trim());
        if (req.getDivision() != null) t.setDivision(req.getDivision());
        if (req.getOccurredAt() != null) t.setOccurredAt(req.getOccurredAt());

        t.setUpdatedAt(Instant.now());
        t = transactionRepository.save(t);
        return toResponse(t);
    }

    public void delete(String userId, String id) {
        Transaction t = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (t.getType() == TransactionType.TRANSFER) {
            throw new ForbiddenException("Transfer transactions cannot be deleted");
        }
        enforceEditWindow(t);

        Account account = accountService.requireOwnedAccount(userId, t.getAccountId());
        revertFromBalance(account, t.getType(), t.getAmount());
        accountService.save(account);

        transactionRepository.delete(t);
    }

    public List<TransactionResponse> recent(String userId, int limit) {
        Query q = new Query(Criteria.where("userId").is(userId));
        q.with(Sort.by(Sort.Direction.DESC, "occurredAt"));
        q.limit(limit);

        List<Transaction> items = mongoTemplate.find(q, Transaction.class);
        return items.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void enforceEditWindow(Transaction t) {
        Instant now = Instant.now();
        Instant created = t.getCreatedAt() == null ? now : t.getCreatedAt();
        long hours = Duration.between(created, now).toHours();
        if (hours >= editWindowHours) {
            throw new ForbiddenException("Editing is restricted after " + editWindowHours + " hours");
        }
    }

    private void applyToBalance(Account account, TransactionType type, double amount) {
        if (amount <= 0) {
            throw new BadRequestException("Amount must be > 0");
        }
        if (type == TransactionType.INCOME) {
            account.setBalance(account.getBalance() + amount);
        } else if (type == TransactionType.EXPENSE) {
            if (account.getBalance() < amount) {
                throw new BadRequestException("Insufficient balance");
            }
            account.setBalance(account.getBalance() - amount);
        }
        accountService.save(account);
    }

    private void revertFromBalance(Account account, TransactionType type, double amount) {
        if (type == TransactionType.INCOME) {
            // removing an income decreases balance
            account.setBalance(account.getBalance() - amount);
        } else if (type == TransactionType.EXPENSE) {
            // removing an expense increases balance back
            account.setBalance(account.getBalance() + amount);
        }
    }

    private void applyDelta(Account account, TransactionType type, double oldAmount, double newAmount) {
        if (type == TransactionType.INCOME) {
            account.setBalance(account.getBalance() + (newAmount - oldAmount));
        } else if (type == TransactionType.EXPENSE) {
            double delta = oldAmount - newAmount; // refund or extra debit
            double candidate = account.getBalance() + delta;
            if (candidate < 0) {
                throw new BadRequestException("Insufficient balance for this update");
            }
            account.setBalance(candidate);
        }
    }

    private TransactionResponse toResponse(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId());
        r.setType(t.getType());
        r.setAmount(t.getAmount());
        r.setDescription(t.getDescription());
        r.setCategory(t.getCategory());
        r.setDivision(t.getDivision());
        r.setAccountId(t.getAccountId());
        r.setFromAccountId(t.getFromAccountId());
        r.setToAccountId(t.getToAccountId());
        r.setOccurredAt(t.getOccurredAt());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());

        // canEdit check
        Instant now = Instant.now();
        Instant created = t.getCreatedAt() == null ? now : t.getCreatedAt();
        long hours = Duration.between(created, now).toHours();
        r.setCanEdit(hours < editWindowHours && t.getType() != TransactionType.TRANSFER);

        return r;
    }
}
