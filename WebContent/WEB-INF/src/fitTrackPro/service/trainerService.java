package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.trainer;
import com.fitTrackPro.util.passwordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class trainerService {
    public int createTrainerAccount(String email, String password, trainer trainer) throws SQLException {
        String findUser = "SELECT u.id AS user_id, t.trainer_id FROM users u LEFT JOIN trainers t ON t.user_id = u.id WHERE u.email = ? OR u.username = ?";
        String insertUser = "INSERT INTO users (username, password, name, email, role, status) VALUES (?, ?, ?, ?, 'TRAINER', 'ACTIVE')";
        String insertTrainer = "INSERT INTO trainers (user_id, first_name, last_name, phone, specialization, experience_years, certification, bio, availability_schedule, hire_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                Integer userId = null;
                try (PreparedStatement ps = connection.prepareStatement(findUser)) {
                    ps.setString(1, email);
                    ps.setString(2, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("trainer_id") > 0) throw new SQLException("A trainer account already exists for that email.", "23000");
                            userId = rs.getInt("user_id");
                        }
                    }
                }

                if (userId == null) {
                    try (PreparedStatement ps = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, email);
                        ps.setString(2, passwordUtil.hashPassword(password));
                        ps.setString(3, trainer.getFullName().trim());
                        ps.setString(4, email);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) userId = keys.getInt(1);
                        }
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE users SET role='TRAINER', status='ACTIVE', name=?, email=?, username=COALESCE(NULLIF(username, ''), ?) WHERE id=?")) {
                        ps.setString(1, trainer.getFullName().trim());
                        ps.setString(2, email);
                        ps.setString(3, email);
                        ps.setInt(4, userId);
                        ps.executeUpdate();
                    }
                }

                if (userId == null) throw new SQLException("Creating trainer user failed.");
                trainer.setUserId(userId);
                try (PreparedStatement ps = connection.prepareStatement(insertTrainer, Statement.RETURN_GENERATED_KEYS)) {
                    bindTrainerInsert(ps, trainer);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            connection.commit();
                            return keys.getInt(1);
                        }
                    }
                }
                throw new SQLException("Creating trainer failed.");
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public boolean updateTrainer(trainer trainer) throws SQLException {
        String updateTrainer = "UPDATE trainers SET first_name=?, last_name=?, phone=?, specialization=?, experience_years=?, certification=?, bio=?, availability_schedule=?, hire_date=?, status=? WHERE trainer_id=?";
        String updateUser = "UPDATE users SET name=? WHERE id=? AND role='TRAINER'";
        try (Connection connection = DBConnection.getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int updated;
                try (PreparedStatement ps = connection.prepareStatement(updateTrainer)) {
                    ps.setString(1, trainer.getFirstName());
                    ps.setString(2, trainer.getLastName());
                    ps.setString(3, trainer.getPhone());
                    ps.setString(4, trainer.getSpecialization());
                    ps.setInt(5, Math.max(0, trainer.getExperienceYears()));
                    ps.setString(6, trainer.getCertification());
                    ps.setString(7, trainer.getBio());
                    ps.setString(8, trainer.getAvailabilitySchedule());
                    ps.setDate(9, trainer.getHireDate());
                    ps.setString(10, trainer.getStatus() == null ? "ACTIVE" : trainer.getStatus());
                    ps.setInt(11, trainer.getTrainerId());
                    updated = ps.executeUpdate();
                }
                if (updated > 0) {
                    try (PreparedStatement ps = connection.prepareStatement(updateUser)) {
                        ps.setString(1, trainer.getFullName().trim());
                        ps.setInt(2, trainer.getUserId());
                        ps.executeUpdate();
                    }
                }
                connection.commit();
                return updated > 0;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public boolean deleteTrainer(int trainerId) throws SQLException {
        String findUser = "SELECT user_id FROM trainers WHERE trainer_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                Integer userId = null;
                try (PreparedStatement ps = connection.prepareStatement(findUser)) {
                    ps.setInt(1, trainerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) userId = rs.getInt("user_id");
                    }
                }
                if (userId == null) {
                    connection.rollback();
                    return false;
                }

                executeDelete(connection, "DELETE FROM workout_plans WHERE trainer_id = ?", trainerId);
                int deleted = executeDelete(connection, "DELETE FROM trainers WHERE trainer_id = ?", trainerId);
                executeDelete(connection, "DELETE FROM users WHERE id = ? AND role = 'TRAINER'", userId);
                connection.commit();
                return deleted > 0;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        }
    }

    public int countAll() throws SQLException {
        try (Connection connection = DBConnection.getConnection(); Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM trainers")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<trainer> findAll() throws SQLException {
        List<trainer> trainers = new ArrayList<>();
        String sql = baseSelect() + " ORDER BY t.created_at DESC";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) trainers.add(mapTrainer(rs));
        }
        return trainers;
    }

    public trainer findById(int trainerId) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(baseSelect() + " WHERE t.trainer_id=?")) {
            ps.setInt(1, trainerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapTrainer(rs) : null;
            }
        }
    }

    public trainer findByUserId(int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(baseSelect() + " WHERE t.user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapTrainer(rs) : null;
            }
        }
    }

    private void bindTrainerInsert(PreparedStatement ps, trainer trainer) throws SQLException {
        ps.setInt(1, trainer.getUserId());
        ps.setString(2, trainer.getFirstName());
        ps.setString(3, trainer.getLastName());
        ps.setString(4, trainer.getPhone());
        ps.setString(5, trainer.getSpecialization());
        ps.setInt(6, Math.max(0, trainer.getExperienceYears()));
        ps.setString(7, trainer.getCertification());
        ps.setString(8, trainer.getBio());
        ps.setString(9, trainer.getAvailabilitySchedule());
        ps.setDate(10, trainer.getHireDate());
        ps.setString(11, trainer.getStatus() == null ? "ACTIVE" : trainer.getStatus());
    }

    private int executeDelete(Connection connection, String sql, int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private String baseSelect() {
        return "SELECT t.*, COALESCE(u.email, u.username) AS email FROM trainers t JOIN users u ON u.id = t.user_id";
    }

    private trainer mapTrainer(ResultSet rs) throws SQLException {
        trainer trainer = new trainer();
        trainer.setTrainerId(rs.getInt("trainer_id"));
        trainer.setUserId(rs.getInt("user_id"));
        trainer.setFirstName(rs.getString("first_name"));
        trainer.setLastName(rs.getString("last_name"));
        trainer.setPhone(rs.getString("phone"));
        trainer.setSpecialization(rs.getString("specialization"));
        trainer.setExperienceYears(rs.getInt("experience_years"));
        trainer.setCertification(rs.getString("certification"));
        trainer.setBio(rs.getString("bio"));
        trainer.setAvailabilitySchedule(rs.getString("availability_schedule"));
        trainer.setHireDate(rs.getDate("hire_date"));
        trainer.setStatus(rs.getString("status"));
        trainer.setCreatedAt(rs.getTimestamp("created_at"));
        trainer.setUpdatedAt(rs.getTimestamp("updated_at"));
        trainer.setEmail(rs.getString("email"));
        return trainer;
    }
}