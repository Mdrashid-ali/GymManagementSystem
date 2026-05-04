package com.fitTrackPro.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public final class dateUtil {
    private static final String DISPLAY_DATE = "MMM dd, yyyy";
    private static final String DISPLAY_DATE_TIME = "MMM dd, yyyy hh:mm a";

    private dateUtil() {
    }

    public static String formatDisplayDate(Date date) {
        return date == null ? "" : new SimpleDateFormat(DISPLAY_DATE).format(date);
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        return timestamp == null ? "" : new SimpleDateFormat(DISPLAY_DATE_TIME).format(timestamp);
    }

    public static Date parseSqlDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Date.valueOf(value);
    }

    public static Date today() {
        return Date.valueOf(LocalDate.now());
    }

    public static Date addMonths(Date date, int months) {
        LocalDate baseDate = date == null ? LocalDate.now() : date.toLocalDate();
        return Date.valueOf(baseDate.plusMonths(months));
    }
}
