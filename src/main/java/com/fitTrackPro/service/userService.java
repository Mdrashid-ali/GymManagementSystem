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
    public user authenticate(String login, String password) throws SQLException {
        String sql = "SELECT id AS user_id, COALESCE(email, username) AS email, COALESCE(NULLIF(name, ''), email, username) AS name, password AS password_hash, role, status, created_at, created_at AS updated_at FROM users WHERE email = ? OR username = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && passwordUtil.verifyPassword(password, rs.getString("password_hash")) ? mapUser(rs) : null;
            }
        }
    }

    public int createUser(String email, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, name, email, role, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, passwordUtil.hashPassword(password));
            ps.setString(3, email);
            ps.setString(4, email);
            ps.setString(5, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Creating user failed, no generated id returned.");
    }

    public boolean emailExists(String email) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM users WHERE email = ? OR username = ?")) {
            ps.setString(1, email);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private user mapUser(ResultSet rs) throws SQLException {
        user user = new user(rs.getInt("user_id"), rs.getString("email"), rs.getString("password_hash"), normalize(rs.getString("role"), "MEMBER"), normalize(rs.getString("status"), "ACTIVE"));
        user.setName(rs.getString("name"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }

    private String normalize(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim().toUpperCase();
    }
}