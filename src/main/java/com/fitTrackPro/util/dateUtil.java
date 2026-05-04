package com.fitTrackPro.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public final class dateUtil {

    private dateUtil() {}

    public static String formatDisplayDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("MMM dd, yyyy").format(date);
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        return timestamp == null ? "" : new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(timestamp);
    }

    public static Date parseSqlDate(String value) {
        return value == null || value.isBlank() ? null : Date.valueOf(value);
    }

    public static Date today() {
        return Date.valueOf(LocalDate.now());
    }

    public static Date addMonths(Date date, int months) {
        return Date.valueOf((date == null ? LocalDate.now() : date.toLocalDate()).plusMonths(months));
    }
}