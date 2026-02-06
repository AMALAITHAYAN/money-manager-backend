package com.moneymanager.repository;

import com.moneymanager.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    List<Account> findByUserIdOrderByCreatedAtAsc(String userId);
    boolean existsByUserIdAndNameIgnoreCase(String userId, String name);
    Optional<Account> findByIdAndUserId(String id, String userId);
}
