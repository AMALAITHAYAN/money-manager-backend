package com.moneymanager.service;

import com.moneymanager.dto.report.CategorySummaryItem;
import com.moneymanager.model.Transaction;
import com.moneymanager.model.enums.Division;
import com.moneymanager.model.enums.TransactionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class ReportService {

    private final MongoTemplate mongoTemplate;

    public ReportService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CategorySummaryItem> categorySummary(String userId, Instant start, Instant end, Division division) {
        Criteria c = Criteria.where("userId").is(userId)
                .and("occurredAt").gte(start).lt(end)
                .and("type").in(TransactionType.INCOME, TransactionType.EXPENSE);

        if (division != null) {
            c = c.and("division").is(division);
        }

        MatchOperation match = match(c);

        ProjectionOperation project = project("category", "type", "amount");

        GroupOperation group = group("category")
                .sum(ConditionalOperators.when(Criteria.where("type").is(TransactionType.INCOME))
                        .thenValueOf("amount").otherwise(0)).as("income")
                .sum(ConditionalOperators.when(Criteria.where("type").is(TransactionType.EXPENSE))
                        .thenValueOf("amount").otherwise(0)).as("expense");

        SortOperation sort = sort(Sort.Direction.ASC, "_id");

        Aggregation agg = newAggregation(match, project, group, sort);

        AggregationResults<CategoryAgg> results = mongoTemplate.aggregate(agg, "transactions", CategoryAgg.class);

        List<CategorySummaryItem> out = new ArrayList<>();
        for (CategoryAgg a : results.getMappedResults()) {
            out.add(new CategorySummaryItem(a.getId(), a.getIncome(), a.getExpense()));
        }
        return out;
    }

    public String exportCsv(String userId, Instant start, Instant end, Division division) {
        Criteria c = Criteria.where("userId").is(userId)
                .and("occurredAt").gte(start).lt(end);

        if (division != null) {
            c = c.and("division").is(division);
        }

        Query q = new Query(c);
        q.with(Sort.by(Sort.Direction.ASC, "occurredAt"));

        List<Transaction> txs = mongoTemplate.find(q, Transaction.class);

        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withHeader(
                "id", "type", "amount", "description", "category", "division",
                "accountId", "fromAccountId", "toAccountId", "occurredAt", "createdAt"
        ))) {
            for (Transaction t : txs) {
                printer.printRecord(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getDescription(),
                        t.getCategory(),
                        t.getDivision(),
                        t.getAccountId(),
                        t.getFromAccountId(),
                        t.getToAccountId(),
                        t.getOccurredAt(),
                        t.getCreatedAt()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static class CategoryAgg {
        private String id;
        private double income;
        private double expense;

        public CategoryAgg() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public double getIncome() { return income; }
        public void setIncome(double income) { this.income = income; }

        public double getExpense() { return expense; }
        public void setExpense(double expense) { this.expense = expense; }
    }
}
