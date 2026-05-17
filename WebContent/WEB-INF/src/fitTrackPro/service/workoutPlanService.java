package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.workoutPlan;
import java.sql.*;
import java.util.*;

public class workoutPlanService {

    public int create(workoutPlan p) throws SQLException {
        String sql = "INSERT INTO workout_plans (trainer_id, member_id, plan_name, description, start_date, end_date, status, exercises, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getTrainerId());
            ps.setInt(2, p.getMemberId());
            ps.setString(3, p.getPlanName());
            ps.setString(4, p.getDescription());
            ps.setDate(5, p.getStartDate());
            ps.setDate(6, p.getEndDate());
            ps.setString(7, p.getStatus() == null ? "ACTIVE" : p.getStatus());
            ps.setString(8, p.getExercises());
            ps.setString(9, p.getNotes());
            ps.executeUpdate();

            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) return k.getInt(1);
            }
        }

        throw new SQLException("Creating workout plan failed.");
    }

    public List<workoutPlan> findByMemberId(int id) throws SQLException {
        return find(" WHERE wp.member_id=? ORDER BY wp.created_at DESC", id);
    }

    public List<workoutPlan> findByTrainerId(int id) throws SQLException {
        return find(" WHERE wp.trainer_id=? ORDER BY wp.created_at DESC", id);
    }

    private List<workoutPlan> find(String where, int id) throws SQLException {
        String sql = "SELECT wp.*, CONCAT(t.first_name,' ',t.last_name) AS trainer_name, CONCAT(m.first_name,' ',m.last_name) AS member_name FROM workout_plans wp JOIN trainers t ON t.trainer_id=wp.trainer_id JOIN members m ON m.member_id=wp.member_id" + where;
        List<workoutPlan> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    workoutPlan p = new workoutPlan();

                    p.setPlanId(rs.getInt("plan_id"));
                    p.setTrainerId(rs.getInt("trainer_id"));
                    p.setMemberId(rs.getInt("member_id"));
                    p.setPlanName(rs.getString("plan_name"));
                    p.setDescription(rs.getString("description"));
                    p.setStartDate(rs.getDate("start_date"));
                    p.setEndDate(rs.getDate("end_date"));
                    p.setStatus(rs.getString("status"));
                    p.setExercises(rs.getString("exercises"));
                    p.setNotes(rs.getString("notes"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));
                    p.setUpdatedAt(rs.getTimestamp("updated_at"));
                    p.setTrainerName(rs.getString("trainer_name"));
                    p.setMemberName(rs.getString("member_name"));

                    list.add(p);
                }
            }
        }

        return list;
    }
}