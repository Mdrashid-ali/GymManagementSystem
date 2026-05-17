package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.util.passwordUtil;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

/**
 * Creates password reset tokens and updates passwords securely.
 */
public class passwordResetService {
	private static final SecureRandom RANDOM = new SecureRandom();

	/** Creates and stores a time-limited password reset token. */
	public String createResetToken(String email) throws SQLException {
		String findUser = "SELECT id FROM users WHERE email = ? OR username = ?";
		String expireOld = "UPDATE password_reset_tokens SET used_at = CURRENT_TIMESTAMP WHERE user_id = ? AND used_at IS NULL";
		String insertToken = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";

		try (Connection connection = DBConnection.getConnection()) {
			Integer userId = null;
			try (PreparedStatement ps = connection.prepareStatement(findUser)) {
				ps.setString(1, email);
				ps.setString(2, email);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						userId = rs.getInt("id");
				}
			}
			if (userId == null)
				return null;

			String token = newToken();
			try (PreparedStatement ps = connection.prepareStatement(expireOld)) {
				ps.setInt(1, userId);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection.prepareStatement(insertToken)) {
				ps.setInt(1, userId);
				ps.setString(2, token);
				ps.setTimestamp(3, Timestamp.from(Instant.now().plus(30, ChronoUnit.MINUTES)));
				ps.executeUpdate();
			}
			return token;
		}
	}

	/** Updates the password for a valid reset token. */
	public boolean resetPassword(String token, String newPassword) throws SQLException {
		String findToken = "SELECT user_id FROM password_reset_tokens WHERE token = ? AND used_at IS NULL AND expires_at > CURRENT_TIMESTAMP";
		String updatePassword = "UPDATE users SET password = ?, failed_login_attempts = 0, locked_until = NULL WHERE id = ?";
		String markUsed = "UPDATE password_reset_tokens SET used_at = CURRENT_TIMESTAMP WHERE token = ?";

		try (Connection connection = DBConnection.getConnection()) {
			boolean oldAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			try {
				Integer userId = null;
				try (PreparedStatement ps = connection.prepareStatement(findToken)) {
					ps.setString(1, token);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next())
							userId = rs.getInt("user_id");
					}
				}
				if (userId == null) {
					connection.rollback();
					return false;
				}
				try (PreparedStatement ps = connection.prepareStatement(updatePassword)) {
					ps.setString(1, passwordUtil.hashPassword(newPassword));
					ps.setInt(2, userId);
					ps.executeUpdate();
				}
				try (PreparedStatement ps = connection.prepareStatement(markUsed)) {
					ps.setString(1, token);
					ps.executeUpdate();
				}
				connection.commit();
				return true;
			} catch (SQLException | RuntimeException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(oldAutoCommit);
			}
		}
	}

	/** Handles newToken logic. */
	private String newToken() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
