package com.fitTrackPro.service;


import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.user;
import com.fitTrackPro.util.passwordUtil;
import com.fitTrackPro.util.validationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Service Class
 * Handles user-related business logic and database operations
 */
public class userService {
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    /**
     * Registers a new user
     */
    public user registerUser(String email, String password, String role) throws SQLException {
        // Validate inputs
        if (!validationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (!passwordUtil.isStrongPassword(password)) {
            throw new IllegalArgumentException(passwordUtil.getPasswordRequirements());
        }
        
        // Check if email already exists
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email address is already registered");
        }
        
        String sql = "INSERT INTO users (email, password_hash, role, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String hashedPassword = passwordUtil.hashPassword(password);
            
            pstmt.setString(1, email.toLowerCase());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            pstmt.setString(4, "ACTIVE");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return getUserById(rs.getInt(1));
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Authenticates a user
     */
    public user authenticateUser(String email, String password, String ipAddress) throws SQLException {
        // Check if account is locked
        if (isAccountLocked(email)) {
            logLoginAttempt(email, ipAddress, false);
            throw new IllegalStateException("Account is temporarily locked. Please try again after " + 
                                           LOCKOUT_DURATION_MINUTES + " minutes.");
        }
        
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user user = mapResultSetToUser(rs);
                    
                    // Check if account is locked
                    if (user.isLocked()) {
                        logLoginAttempt(email, ipAddress, false);
                        throw new IllegalStateException("Account is locked. Please contact support.");
                    }
                    
                    // Verify password
                    if (passwordUtil.verifyPassword(password, user.getPasswordHash())) {
                        // Reset failed attempts on successful login
                        logLoginAttempt(email, ipAddress, true);
                        return user;
                    } else {
                        logLoginAttempt(email, ipAddress, false);
                        
                        // Check if account should be locked
                        if (getRecentFailedAttempts(email) >= MAX_LOGIN_ATTEMPTS) {
                            lockAccount(email);
                            throw new IllegalStateException("Too many failed attempts. Account locked for " + 
                                                           LOCKOUT_DURATION_MINUTES + " minutes.");
                        }
                    }
                } else {
                    logLoginAttempt(email, ipAddress, false);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets user by ID
     */
    public user getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets user by email
     */
    public user getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets all users
     */
    public List<user> getAllUsers() throws SQLException {
        List<user> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Checks if email exists
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Updates user password
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        if (!passwordUtil.isStrongPassword(newPassword)) {
            throw new IllegalArgumentException(passwordUtil.getPasswordRequirements());
        }
        
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, passwordUtil.hashPassword(newPassword));
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Updates user status
     */
    public boolean updateUserStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Locks a user account
     */
    public void lockAccount(String email) throws SQLException {
        String sql = "UPDATE users SET status = 'LOCKED' WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Unlocks a user account
     */
    public boolean unlockAccount(String email) throws SQLException {
        String sql = "UPDATE users SET status = 'ACTIVE' WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Checks if account is locked
     */
    public boolean isAccountLocked(String email) throws SQLException {
        String sql = "SELECT status FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "LOCKED".equals(rs.getString("status"));
                }
            }
        }
        
        return false;
    }
    
    /**
     * Logs login attempt
     */
    public void logLoginAttempt(String email, String ipAddress, boolean isSuccessful) throws SQLException {
        String sql = "INSERT INTO login_attempts (email, ip_address, is_successful) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            pstmt.setString(2, ipAddress);
            pstmt.setBoolean(3, isSuccessful);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Gets recent failed login attempts
     */
    public int getRecentFailedAttempts(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM login_attempts " +
                    "WHERE email = ? AND is_successful = FALSE " +
                    "AND attempt_time > DATE_SUB(NOW(), INTERVAL ? MINUTE)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            pstmt.setInt(2, LOCKOUT_DURATION_MINUTES);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Creates a password reset token
     */
    public String createPasswordResetToken(String email) throws SQLException {
        user user = getUserByEmail(email);
        if (user == null) {
            return null;
        }
        
        String token = passwordUtil.generateResetToken();
        String sql = "INSERT INTO password_reset_tokens (user_id, token, expiry_date) " +
                    "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 1 HOUR))";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, token);
            
            if (pstmt.executeUpdate() > 0) {
                return token;
            }
        }
        
        return null;
    }
    
    /**
     * Validates a password reset token
     */
    public Integer validateResetToken(String token) throws SQLException {
        String sql = "SELECT user_id FROM password_reset_tokens " +
                    "WHERE token = ? AND expiry_date > NOW() AND is_used = FALSE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        
        return null;
    }
    
    /**
     * Marks a reset token as used
     */
    public void markResetTokenUsed(String token) throws SQLException {
        String sql = "UPDATE password_reset_tokens SET is_used = TRUE WHERE token = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, token);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a user
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Maps ResultSet to User object
     */
    private user mapResultSetToUser(ResultSet rs) throws SQLException {
        user user = new user();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}