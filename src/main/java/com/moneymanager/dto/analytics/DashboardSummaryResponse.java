package com.moneymanager.dto.analytics;

import com.moneymanager.dto.transaction.TransactionResponse;

import java.time.Instant;
import java.util.List;

public class DashboardSummaryResponse {
    private Instant start;
    private Instant end;

    private double totalIncome;
    private double totalExpense;
    private double net;

    private List<ChartPoint> points;
    private List<TransactionResponse> recent;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(Instant start, Instant end, double totalIncome, double totalExpense, double net,
                                    List<ChartPoint> points, List<TransactionResponse> recent) {
        this.start = start;
        this.end = end;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.net = net;
        this.points = points;
        this.recent = recent;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public List<ChartPoint> getPoints() {
        return points;
    }

    public void setPoints(List<ChartPoint> points) {
        this.points = points;
    }

    public List<TransactionResponse> getRecent() {
        return recent;
    }

    public void setRecent(List<TransactionResponse> recent) {
        this.recent = recent;
    }
}
