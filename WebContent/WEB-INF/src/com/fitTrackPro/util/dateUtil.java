package com.fitTrackPro.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * Provides date parsing and display formatting helpers.
 */
public final class dateUtil {

	private dateUtil() {
	}

	/** Formats a SQL date for display in JSP pages. */
	public static String formatDisplayDate(Date date) {
		return date == null ? "" : new SimpleDateFormat("MMM dd, yyyy").format(date);
	}

	/** Handles formatDisplayDateTime logic. */
	public static String formatDisplayDateTime(Timestamp timestamp) {
		return timestamp == null ? "" : new SimpleDateFormat("MMM dd, yyyy hh:mm a").format(timestamp);
	}

	/** Parses a request date value into a SQL Date. */
	public static Date parseSqlDate(String value) {
		return value == null || value.isBlank() ? null : Date.valueOf(value);
	}

	/** Converts the value. */
	public static Date today() {
		return Date.valueOf(LocalDate.now());
	}

	/** Handles addMonths logic. */
	public static Date addMonths(Date date, int months) {
		return Date.valueOf((date == null ? LocalDate.now() : date.toLocalDate()).plusMonths(months));
	}
}
