package com.fitTrackPro.util;

import java.util.regex.Pattern;

/**
 * Provides reusable form validation helpers.
 */
public final class validationUtil {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	private static final Pattern STRONG_PASSWORD_PATTERN = Pattern
			.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

	private validationUtil() {
	}

	/** Checks whether a string is null or empty after trimming. */
	public static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	/** Validates an email address format. */
	public static boolean isValidEmail(String email) {
		return !isBlank(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
	}

	/** Validates password strength requirements. */
	public static boolean isStrongPassword(String password) {
		return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
	}

	/** Validates phone number input. */
	public static boolean isValidPhone(String phone) {
		return !isBlank(phone) && phone.replaceAll("[\\s()+-]", "").matches("\\d{7,15}");
	}
}
