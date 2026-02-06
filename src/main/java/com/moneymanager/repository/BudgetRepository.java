package com.moneymanager.repository;

import com.moneymanager.model.Budget;
import com.moneymanager.model.enums.Division;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, String> {

    Optional<Budget> findByUserIdAndMonthAndDivisionAndCategoryIgnoreCase(
            String userId, String month, Division division, String category);

    List<Budget> findByUserIdAndMonthOrderByCategoryAsc(String userId, String month);

    List<Budget> findByUserIdAndMonthAndDivisionOrderByCategoryAsc(String userId, String month, Division division);
}
