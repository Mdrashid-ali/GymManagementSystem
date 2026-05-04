package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.user;
import com.fitTrackPro.util.passwordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class userService {
    public user authenticate(String emailOrUsername, String password) throws SQLException {
        String sql = "SELECT id AS user_id, email, username, password AS password_hash, role, status, created_at, created_at AS updated_at "
                + "FROM users WHERE email = ? OR username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next() && passwordUtil.verifyPassword(password, rs.getString("password_hash"))) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public int createUser(String email, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, name, email, role, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, email);
            statement.setString(2, passwordUtil.hashPassword(password));
            statement.setString(3, email);
            statement.setString(4, email);
            statement.setString(5, role);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating user failed, no generated id returned.");
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? OR username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, email);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private user mapUser(ResultSet rs) throws SQLException {
        String email = rs.getString("email");
        if (email == null || email.isBlank()) {
            email = rs.getString("username");
        }
        user result = new user(
                rs.getInt("user_id"),
                email,
                rs.getString("password_hash"),
                normalizeRole(rs.getString("role")),
                normalizeStatus(rs.getString("status")));
        result.setCreatedAt(rs.getTimestamp("created_at"));
        result.setUpdatedAt(rs.getTimestamp("updated_at"));
        return result;
    }

    private String normalizeRole(String role) {
        return role == null || role.isBlank() ? "MEMBER" : role.trim().toUpperCase();
    }

    private String normalizeStatus(String status) {
        return status == null || status.isBlank() ? "ACTIVE" : status.trim().toUpperCase();
    }
}