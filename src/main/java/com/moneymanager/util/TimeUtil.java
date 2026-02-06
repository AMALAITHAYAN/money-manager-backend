package com.moneymanager.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private TimeUtil() {}

    public static String currentYearMonth() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC);
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    public static Instant startOfMonthUtc(String yearMonth) {
        // yearMonth format: yyyy-MM
        String[] parts = yearMonth.split("-");
        int y = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return ZonedDateTime.of(y, m, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
    }

    public static Instant startOfNextMonthUtc(String yearMonth) {
        String[] parts = yearMonth.split("-");
        int y = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        ZonedDateTime start = ZonedDateTime.of(y, m, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        return start.plusMonths(1).toInstant();
    }
}
