package com.moneymanager.repository;

import com.moneymanager.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Optional<Transaction> findByIdAndUserId(String id, String userId);
}
