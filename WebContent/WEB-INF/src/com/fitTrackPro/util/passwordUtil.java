package com.fitTrackPro.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides password hashing and verification helpers.
 */
public final class passwordUtil {
	private passwordUtil() {
	}

	/** Hashes a plain text password using SHA-256. */
	public static String hashPassword(String password) {
		if (password == null)
			throw new IllegalArgumentException("Password must not be null");
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder result = new StringBuilder(hash.length * 2);
			for (byte b : hash)
				result.append(String.format("%02x", b));
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 is not available", e);
		}
	}

	/** Compares a submitted password with a stored password hash. */
	public static boolean verifyPassword(String password, String storedHash) {
		if (password == null || storedHash == null)
			return false;
		return hashPassword(password).equalsIgnoreCase(storedHash) || password.equals(storedHash);
	}
}
