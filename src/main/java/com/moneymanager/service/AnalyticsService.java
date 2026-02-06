package com.moneymanager.service;

import com.moneymanager.dto.analytics.ChartPoint;
import com.moneymanager.dto.analytics.DashboardSummaryResponse;
import com.moneymanager.dto.transaction.TransactionResponse;
import com.moneymanager.model.Transaction;
import com.moneymanager.model.enums.Division;
import com.moneymanager.model.enums.TransactionType;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;
    private final TransactionService transactionService;

    public AnalyticsService(MongoTemplate mongoTemplate, TransactionService transactionService) {
        this.mongoTemplate = mongoTemplate;
        this.transactionService = transactionService;
    }

    public DashboardSummaryResponse dashboard(
            String userId,
            String period,
            Instant start,
            Instant end,
            Division division
    ) {
        // Default ranges
        Instant now = Instant.now();
        if (start == null || end == null) {
            Range r = defaultRange(period, now);
            start = start == null ? r.start : start;
            end = end == null ? r.end : end;
        }

        String groupFormat = groupFormat(period);

        Criteria matchCriteria = Criteria.where("userId").is(userId)
                .and("occurredAt").gte(start).lt(end)
                .and("type").in(TransactionType.INCOME, TransactionType.EXPENSE);
        if (division != null) {
            matchCriteria = matchCriteria.and("division").is(division);
        }

        MatchOperation match = match(matchCriteria);

        // Create group key as formatted date
        ProjectionOperation project = project("type", "amount")
                .and(DateOperators.dateOf("occurredAt").withTimezone(DateOperators.Timezone.valueOf("UTC"))
                        .toString(groupFormat)).as("bucket");

        // Conditional sums
        GroupOperation group = group("bucket")
                .sum(ConditionalOperators.when(Criteria.where("type").is(TransactionType.INCOME))
                        .thenValueOf("amount").otherwise(0)).as("income")
                .sum(ConditionalOperators.when(Criteria.where("type").is(TransactionType.EXPENSE))
                        .thenValueOf("amount").otherwise(0)).as("expense");

        SortOperation sort = sort(Sort.Direction.ASC, "_id");

        Aggregation agg = newAggregation(match, project, group, sort);

        AggregationResults<BucketAgg> results = mongoTemplate.aggregate(agg, "transactions", BucketAgg.class);

        List<ChartPoint> points = new ArrayList<>();
        double totalIncome = 0;
        double totalExpense = 0;

        for (BucketAgg b : results.getMappedResults()) {
            points.add(new ChartPoint(b.getId(), b.getIncome(), b.getExpense()));
            totalIncome += b.getIncome();
            totalExpense += b.getExpense();
        }

        double net = totalIncome - totalExpense;

        List<TransactionResponse> recent = transactionService.recent(userId, 10);
        return new DashboardSummaryResponse(start, end, totalIncome, totalExpense, net, points, recent);
    }

    private String groupFormat(String period) {
        if (period == null) return "%Y-%m-%d";
        String p = period.trim().toLowerCase();
        if (p.equals("year")) return "%Y-%m";
        return "%Y-%m-%d"; // week/month/day-level
    }

    private Range defaultRange(String period, Instant now) {
        String p = period == null ? "month" : period.trim().toLowerCase();
        ZonedDateTime z = ZonedDateTime.ofInstant(now, ZoneOffset.UTC);
        if (p.equals("week")) {
            Instant start = z.minusDays(6).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = z.plusDays(1).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
            return new Range(start, end);
        }
        if (p.equals("year")) {
            Instant start = ZonedDateTime.of(z.getYear(), 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
            Instant end = ZonedDateTime.of(z.getYear() + 1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
            return new Range(start, end);
        }
        // default month
        Instant start = ZonedDateTime.of(z.getYear(), z.getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
        Instant end = ZonedDateTime.of(z.getYear(), z.getMonthValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC).plusMonths(1).toInstant();
        return new Range(start, end);
    }

    private static class Range {
        Instant start;
        Instant end;
        Range(Instant start, Instant end) { this.start = start; this.end = end; }
    }

    public static class BucketAgg {
        private String id; // _id
        private double income;
        private double expense;

        public BucketAgg() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public double getIncome() { return income; }
        public void setIncome(double income) { this.income = income; }

        public double getExpense() { return expense; }
        public void setExpense(double expense) { this.expense = expense; }
    }
}
