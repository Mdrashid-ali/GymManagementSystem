package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.trainer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class trainerService {
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM trainers";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public trainer findByUserId(int userId) throws SQLException {
        String sql = "SELECT t.*, u.email FROM trainers t JOIN users u ON u.id = t.user_id WHERE t.user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                trainer result = new trainer();
                result.setTrainerId(rs.getInt("trainer_id"));
                result.setUserId(rs.getInt("user_id"));
                result.setFirstName(rs.getString("first_name"));
                result.setLastName(rs.getString("last_name"));
                result.setPhone(rs.getString("phone"));
                result.setSpecialization(rs.getString("specialization"));
                result.setExperienceYears(rs.getInt("experience_years"));
                result.setCertification(rs.getString("certification"));
                result.setBio(rs.getString("bio"));
                result.setAvailabilitySchedule(rs.getString("availability_schedule"));
                result.setHireDate(rs.getDate("hire_date"));
                result.setStatus(rs.getString("status"));
                result.setCreatedAt(rs.getTimestamp("created_at"));
                result.setUpdatedAt(rs.getTimestamp("updated_at"));
                result.setEmail(rs.getString("email"));
                return result;
            }
        }
    }
}
