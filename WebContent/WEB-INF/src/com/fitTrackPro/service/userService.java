package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.user;
import com.fitTrackPro.util.passwordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Performs user authentication, account lockout, and basic user lookup
 * operations.
 */
public class userService {
	private static final int MAX_FAILED_ATTEMPTS = 3;
	private static final int LOCK_MINUTES = 2;
	private static final DateTimeFormatter LOCK_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");

	/** Authenticates a user and applies failed-login lockout rules. */
	public user authenticate(String login, String password) throws SQLException, AccountLockedException {
		String sql = "SELECT id AS user_id, COALESCE(email, username) AS email, COALESCE(NULLIF(name, ''), email, username) AS name, password AS password_hash, role, status, created_at, created_at AS updated_at, failed_login_attempts, locked_until FROM users WHERE email = ? OR username = ?";
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, login);
			ps.setString(2, login);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next())
					return null;

				int userId = rs.getInt("user_id");
				Timestamp lockedUntil = rs.getTimestamp("locked_until");
				Timestamp now = new Timestamp(System.currentTimeMillis());

				if (lockedUntil != null && lockedUntil.after(now)) {
					throw new AccountLockedException("Account locked after 3 wrong password attempts. Try again after "
							+ formatLockTime(lockedUntil) + ".");
				}

				if (lockedUntil != null) {
					resetLoginLock(connection, userId);
				}

				if (passwordUtil.verifyPassword(password, rs.getString("password_hash"))) {
					resetLoginLock(connection, userId);
					return mapUser(rs);
				}

				int attempts = rs.getInt("failed_login_attempts") + 1;
				if (attempts >= MAX_FAILED_ATTEMPTS) {
					Timestamp newLockedUntil = Timestamp.valueOf(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
					lockAccount(connection, userId, attempts, newLockedUntil);
					throw new AccountLockedException(
							"Account locked for 2 minutes after 3 wrong password attempts. Try again after "
									+ formatLockTime(newLockedUntil) + ".");
				}

				recordFailedAttempt(connection, userId, attempts);
				return null;
			}
		}
	}

	/** Creates a new application user record. */
	public int createUser(String email, String password, String role) throws SQLException {
		String sql = "INSERT INTO users (username, password, name, email, role, status, failed_login_attempts, locked_until) VALUES (?, ?, ?, ?, ?, 'ACTIVE', 0, NULL)";
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, email);
			ps.setString(2, passwordUtil.hashPassword(password));
			ps.setString(3, email);
			ps.setString(4, email);
			ps.setString(5, role);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next())
					return keys.getInt(1);
			}
		}
		throw new SQLException("Creating user failed, no generated id returned.");
	}

	/** Checks whether an email or username is already registered. */
	public boolean emailExists(String email) throws SQLException {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection
						.prepareStatement("SELECT 1 FROM users WHERE email = ? OR username = ?")) {
			ps.setString(1, email);
			ps.setString(2, email);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	/** Handles recordFailedAttempt logic. */
	private void recordFailedAttempt(Connection connection, int userId, int attempts) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("UPDATE users SET failed_login_attempts = ?, locked_until = NULL WHERE id = ?")) {
			ps.setInt(1, attempts);
			ps.setInt(2, userId);
			ps.executeUpdate();
		}
	}

	/** Handles lockAccount logic. */
	private void lockAccount(Connection connection, int userId, int attempts, Timestamp lockedUntil)
			throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("UPDATE users SET failed_login_attempts = ?, locked_until = ? WHERE id = ?")) {
			ps.setInt(1, attempts);
			ps.setTimestamp(2, lockedUntil);
			ps.setInt(3, userId);
			ps.executeUpdate();
		}
	}

	/** Handles resetLoginLock logic. */
	private void resetLoginLock(Connection connection, int userId) throws SQLException {
		try (PreparedStatement ps = connection
				.prepareStatement("UPDATE users SET failed_login_attempts = 0, locked_until = NULL WHERE id = ?")) {
			ps.setInt(1, userId);
			ps.executeUpdate();
		}
	}

	/** Handles formatLockTime logic. */
	private String formatLockTime(Timestamp lockedUntil) {
		return lockedUntil.toLocalDateTime().format(LOCK_TIME_FORMAT);
	}

	/** Handles mapUser logic. */
	private user mapUser(ResultSet rs) throws SQLException {
		user user = new user(rs.getInt("user_id"), rs.getString("email"), rs.getString("password_hash"),
				normalize(rs.getString("role"), "MEMBER"), normalize(rs.getString("status"), "ACTIVE"));
		user.setName(rs.getString("name"));
		user.setCreatedAt(rs.getTimestamp("created_at"));
		user.setUpdatedAt(rs.getTimestamp("updated_at"));
		return user;
	}

	/** Handles normalize logic. */
	private String normalize(String value, String defaultValue) {
		return value == null || value.isBlank() ? defaultValue : value.trim().toUpperCase();
	}

	public static class AccountLockedException extends Exception {
		public AccountLockedException(String message) {
			super(message);
		}
	}
}
