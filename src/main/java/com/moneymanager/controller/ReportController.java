package com.moneymanager.controller;

import com.moneymanager.dto.report.CategorySummaryItem;
import com.moneymanager.model.enums.Division;
import com.moneymanager.service.ReportService;
import com.moneymanager.util.SecurityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/categories")
    public List<CategorySummaryItem> categorySummary(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(value = "division", required = false) Division division
    ) {
        String userId = SecurityUtil.requireUserId();
        return reportService.categorySummary(userId, start, end, division);
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(value = "division", required = false) Division division
    ) {
        String userId = SecurityUtil.requireUserId();
        String csv = reportService.exportCsv(userId, start, end, division);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .contentType(MediaType.valueOf("text/csv"))
                .body(csv);
    }
}
