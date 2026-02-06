package com.moneymanager.controller;

import com.moneymanager.dto.analytics.DashboardSummaryResponse;
import com.moneymanager.model.enums.Division;
import com.moneymanager.service.AnalyticsService;
import com.moneymanager.util.SecurityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public DashboardSummaryResponse dashboard(
            @RequestParam(value = "period", defaultValue = "month") String period,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(value = "division", required = false) Division division
    ) {
        String userId = SecurityUtil.requireUserId();
        return analyticsService.dashboard(userId, period, start, end, division);
    }
}
