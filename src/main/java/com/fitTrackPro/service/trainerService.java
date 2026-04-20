package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.trainer;
import com.fitTrackPro.model.workoutPlan;
import com.fitTrackPro.util.validationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Trainer Service Class
 * Handles trainer-related business logic
 */
public class trainerService {
    
    /**
     * Gets trainer by ID
     */
    public trainer getTrainerById(int trainerId) throws SQLException {
        String sql = "SELECT t.*, u.email, u.status as user_status " +
                    "FROM trainers t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "WHERE t.trainer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trainerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrainer(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets trainer by user ID
     */
    public trainer getTrainerByUserId(int userId) throws SQLException {
        String sql = "SELECT t.*, u.email, u.status as user_status " +
                    "FROM trainers t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "WHERE t.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrainer(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets all trainers
     */
    public List<trainer> getAllTrainers() throws SQLException {
        List<trainer> trainers = new ArrayList<>();
        String sql = "SELECT t.*, u.email, u.status as user_status " +
                    "FROM trainers t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "ORDER BY t.first_name, t.last_name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                trainers.add(mapResultSetToTrainer(rs));
            }
        }
        
        return trainers;
    }
    
    /**
     * Gets active trainers
     */
    public List<trainer> getActiveTrainers() throws SQLException {
        List<trainer> trainers = new ArrayList<>();
        String sql = "SELECT t.*, u.email, u.status as user_status " +
                    "FROM trainers t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "WHERE t.status = 'ACTIVE' AND u.status = 'ACTIVE' " +
                    "ORDER BY t.first_name, t.last_name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                trainers.add(mapResultSetToTrainer(rs));
            }
        }
        
        return trainers;
    }
    
    /**
     * Creates a workout plan
     */
    public int createWorkoutPlan(workoutPlan plan) throws SQLException {
        String sql = "INSERT INTO workout_plans (trainer_id, member_id, plan_name, " +
                    "description, start_date, end_date, exercises, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, plan.getTrainerId());
            pstmt.setInt(2, plan.getMemberId());
            pstmt.setString(3, plan.getPlanName());
            pstmt.setString(4, plan.getDescription());
            pstmt.setDate(5, plan.getStartDate());
            pstmt.setDate(6, plan.getEndDate());
            pstmt.setString(7, plan.getExercises());
            pstmt.setString(8, plan.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Gets workout plans by trainer
     */
    public List<workoutPlan> getWorkoutPlansByTrainer(int trainerId) throws SQLException {
        List<workoutPlan> plans = new ArrayList<>();
        String sql = "SELECT wp.*, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as member_name, " +
                    "CONCAT(t.first_name, ' ', t.last_name) as trainer_name " +
                    "FROM workout_plans wp " +
                    "JOIN members m ON wp.member_id = m.member_id " +
                    "JOIN trainers t ON wp.trainer_id = t.trainer_id " +
                    "WHERE wp.trainer_id = ? " +
                    "ORDER BY wp.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trainerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToWorkoutPlan(rs));
                }
            }
        }
        
        return plans;
    }
    
    /**
     * Gets workout plans by member
     */
    public List<workoutPlan> getWorkoutPlansByMember(int memberId) throws SQLException {
        List<workoutPlan> plans = new ArrayList<>();
        String sql = "SELECT wp.*, " +
                    "CONCAT(m.first_name, ' ', m.last_name) as member_name, " +
                    "CONCAT(t.first_name, ' ', t.last_name) as trainer_name " +
                    "FROM workout_plans wp " +
                    "JOIN members m ON wp.member_id = m.member_id " +
                    "JOIN trainers t ON wp.trainer_id = t.trainer_id " +
                    "WHERE wp.member_id = ? " +
                    "ORDER BY wp.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToWorkoutPlan(rs));
                }
            }
        }
        
        return plans;
    }
    
    /**
     * Maps ResultSet to Trainer object
     */
    private trainer mapResultSetToTrainer(ResultSet rs) throws SQLException {
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
        
        try {
            trainer.setEmail(rs.getString("email"));
        } catch (SQLException e) {
            // Email might not be in all queries
        }
        
        return trainer;
    }
    
    /**
     * Maps ResultSet to WorkoutPlan object
     */
    private workoutPlan mapResultSetToWorkoutPlan(ResultSet rs) throws SQLException {
        workoutPlan plan = new workoutPlan();
        plan.setPlanId(rs.getInt("plan_id"));
        plan.setTrainerId(rs.getInt("trainer_id"));
        plan.setMemberId(rs.getInt("member_id"));
        plan.setPlanName(rs.getString("plan_name"));
        plan.setDescription(rs.getString("description"));
        plan.setStartDate(rs.getDate("start_date"));
        plan.setEndDate(rs.getDate("end_date"));
        plan.setStatus(rs.getString("status"));
        plan.setExercises(rs.getString("exercises"));
        plan.setNotes(rs.getString("notes"));
        plan.setCreatedAt(rs.getTimestamp("created_at"));
        plan.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        try {
            plan.setMemberName(rs.getString("member_name"));
            plan.setTrainerName(rs.getString("trainer_name"));
        } catch (SQLException e) {
            // These fields might not be in all queries
        }
        
        return plan;
    }
}