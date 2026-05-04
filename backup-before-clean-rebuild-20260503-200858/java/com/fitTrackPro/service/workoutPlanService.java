package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.workoutPlan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class workoutPlanService {
    public int create(workoutPlan plan) throws SQLException {
        String sql = "INSERT INTO workout_plans (trainer_id, member_id, plan_name, description, start_date, end_date, "
                + "status, exercises, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, plan.getTrainerId());
            statement.setInt(2, plan.getMemberId());
            statement.setString(3, plan.getPlanName());
            statement.setString(4, plan.getDescription());
            statement.setDate(5, plan.getStartDate());
            statement.setDate(6, plan.getEndDate());
            statement.setString(7, plan.getStatus() == null ? "ACTIVE" : plan.getStatus());
            statement.setString(8, plan.getExercises());
            statement.setString(9, plan.getNotes());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating workout plan failed, no generated id returned.");
    }

    public List<workoutPlan> findByMemberId(int memberId) throws SQLException {
        return find(" WHERE wp.member_id = ? ORDER BY wp.created_at DESC", memberId);
    }

    public List<workoutPlan> findByTrainerId(int trainerId) throws SQLException {
        return find(" WHERE wp.trainer_id = ? ORDER BY wp.created_at DESC", trainerId);
    }

    private List<workoutPlan> find(String whereClause, int id) throws SQLException {
        String sql = "SELECT wp.*, CONCAT(t.first_name, ' ', t.last_name) AS trainer_name, "
                + "CONCAT(m.first_name, ' ', m.last_name) AS member_name "
                + "FROM workout_plans wp "
                + "JOIN trainers t ON t.trainer_id = wp.trainer_id "
                + "JOIN members m ON m.member_id = wp.member_id "
                + whereClause;
        List<workoutPlan> plans = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapWorkoutPlan(rs));
                }
            }
        }
        return plans;
    }

    private workoutPlan mapWorkoutPlan(ResultSet rs) throws SQLException {
        workoutPlan result = new workoutPlan();
        result.setPlanId(rs.getInt("plan_id"));
        result.setTrainerId(rs.getInt("trainer_id"));
        result.setMemberId(rs.getInt("member_id"));
        result.setPlanName(rs.getString("plan_name"));
        result.setDescription(rs.getString("description"));
        result.setStartDate(rs.getDate("start_date"));
        result.setEndDate(rs.getDate("end_date"));
        result.setStatus(rs.getString("status"));
        result.setExercises(rs.getString("exercises"));
        result.setNotes(rs.getString("notes"));
        result.setCreatedAt(rs.getTimestamp("created_at"));
        result.setUpdatedAt(rs.getTimestamp("updated_at"));
        result.setTrainerName(rs.getString("trainer_name"));
        result.setMemberName(rs.getString("member_name"));
        return result;
    }
}
