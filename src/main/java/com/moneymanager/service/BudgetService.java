package com.moneymanager.service;

import com.moneymanager.dto.budget.BudgetResponse;
import com.moneymanager.dto.budget.BudgetStatusItem;
import com.moneymanager.dto.budget.UpsertBudgetRequest;
import com.moneymanager.exception.BadRequestException;
import com.moneymanager.model.Budget;
import com.moneymanager.model.enums.Division;
import com.moneymanager.model.enums.TransactionType;
import com.moneymanager.repository.BudgetRepository;
import com.moneymanager.util.TimeUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final MongoTemplate mongoTemplate;

    public BudgetService(BudgetRepository budgetRepository, MongoTemplate mongoTemplate) {
        this.budgetRepository = budgetRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public BudgetResponse upsert(String userId, UpsertBudgetRequest req) {
        String month = (req.getMonth() == null || req.getMonth().isBlank())
                ? TimeUtil.currentYearMonth()
                : req.getMonth().trim();

        String category = req.getCategory().trim();

        Budget b = budgetRepository.findByUserIdAndMonthAndDivisionAndCategoryIgnoreCase(
                userId, month, req.getDivision(), category
        ).orElse(null);

        if (b == null) {
            b = new Budget(userId, month, req.getDivision(), category, req.getLimitAmount());
        } else {
            b.setLimitAmount(req.getLimitAmount());
            b.setUpdatedAt(Instant.now());
        }

        b = budgetRepository.save(b);
        return toResponse(b);
    }

    public List<BudgetResponse> list(String userId, String month, Division division) {
        if (month == null || month.isBlank()) {
            month = TimeUtil.currentYearMonth();
        }
        List<Budget> items;
        if (division == null) {
            items = budgetRepository.findByUserIdAndMonthOrderByCategoryAsc(userId, month);
        } else {
            items = budgetRepository.findByUserIdAndMonthAndDivisionOrderByCategoryAsc(userId, month, division);
        }
        return items.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<BudgetStatusItem> status(String userId, String month, Division division) {
        if (division == null) {
            throw new BadRequestException("division is required for budget status");
        }
        if (month == null || month.isBlank()) {
            month = TimeUtil.currentYearMonth();
        }
        Instant start = TimeUtil.startOfMonthUtc(month);
        Instant end = TimeUtil.startOfNextMonthUtc(month);

        Map<String, Double> spentByCategory = computeSpentByCategory(userId, start, end, division);

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndDivisionOrderByCategoryAsc(userId, month, division);

        List<BudgetStatusItem> out = new ArrayList<>();
        for (Budget b : budgets) {
            double spent = spentByCategory.getOrDefault(b.getCategory().toLowerCase(), 0.0);
            out.add(new BudgetStatusItem(b.getMonth(), b.getDivision(), b.getCategory(), b.getLimitAmount(), spent));
        }
        return out;
    }

    private Map<String, Double> computeSpentByCategory(String userId, Instant start, Instant end, Division division) {
        Criteria c = Criteria.where("userId").is(userId)
                .and("occurredAt").gte(start).lt(end)
                .and("type").is(TransactionType.EXPENSE)
                .and("division").is(division);

        MatchOperation match = match(c);

        GroupOperation group = group("category").sum("amount").as("spent");

        Aggregation agg = newAggregation(match, group);

        AggregationResults<SpentAgg> results = mongoTemplate.aggregate(agg, "transactions", SpentAgg.class);

        Map<String, Double> map = new HashMap<>();
        for (SpentAgg a : results.getMappedResults()) {
            map.put(a.getId() == null ? "" : a.getId().toLowerCase(), a.getSpent());
        }
        return map;
    }

    private BudgetResponse toResponse(Budget b) {
        return new BudgetResponse(b.getId(), b.getMonth(), b.getDivision(), b.getCategory(), b.getLimitAmount(),
                b.getCreatedAt(), b.getUpdatedAt());
    }

    public static class SpentAgg {
        private String id;
        private double spent;

        public SpentAgg() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public double getSpent() { return spent; }
        public void setSpent(double spent) { this.spent = spent; }
    }
}
