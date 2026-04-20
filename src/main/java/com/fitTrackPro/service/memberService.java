package com.fitTrackPro.service;


import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.attendance;
import com.fitTrackPro.model.member;
import com.fitTrackPro.model.user;
import com.fitTrackPro.util.validationUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Member Service Class
 * Handles member-related business logic
 */
public class memberService {
    
    private userService userService = new userService();
    
    public memberService() {
        this.userService = new userService();
    }
    
    /**
     * Registers a new member with user account
     */
    public member registerMember(member member, String password) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Create user account first
            user user = userService.registerUser(member.getEmail(), password, "MEMBER");
            if (user == null) {
                throw new SQLException("Failed to create user account");
            }
            
            // Create member record
            String sql = "INSERT INTO members (user_id, first_name, last_name, phone, " +
                        "date_of_birth, gender, address, emergency_contact_name, " +
                        "emergency_contact_phone, membership_type, join_date, " +
                        "membership_expiry_date, height_cm, weight_kg, fitness_goal, medical_notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, user.getUserId());
                pstmt.setString(2, member.getFirstName());
                pstmt.setString(3, member.getLastName());
                pstmt.setString(4, validationUtil.cleanPhoneNumber(member.getPhone()));
                pstmt.setDate(5, member.getDateOfBirth());
                pstmt.setString(6, member.getGender());
                pstmt.setString(7, member.getAddress());
                pstmt.setString(8, member.getEmergencyContactName());
                pstmt.setString(9, validationUtil.cleanPhoneNumber(member.getEmergencyContactPhone()));
                pstmt.setString(10, member.getMembershipType());
                pstmt.setDate(11, member.getJoinDate());
                pstmt.setDate(12, member.getMembershipExpiryDate());
                pstmt.setDouble(13, member.getHeightCm() != null ? member.getHeightCm() : 0);
                pstmt.setDouble(14, member.getWeightKg() != null ? member.getWeightKg() : 0);
                pstmt.setString(15, member.getFitnessGoal());
                pstmt.setString(16, member.getMedicalNotes());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            conn.commit();
                            return getMemberById(rs.getInt(1));
                        }
                    }
                }
            }
            
            conn.rollback();
            return null;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Gets member by ID
     */
    public member getMemberById(int memberId) throws SQLException {
        String sql = "SELECT m.*, u.email, u.status as user_status " +
                    "FROM members m " +
                    "JOIN users u ON m.user_id = u.user_id " +
                    "WHERE m.member_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets member by user ID
     */
    public member getMemberByUserId(int userId) throws SQLException {
        String sql = "SELECT m.*, u.email, u.status as user_status " +
                    "FROM members m " +
                    "JOIN users u ON m.user_id = u.user_id " +
                    "WHERE m.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Gets all members
     */
    public List<member> getAllMembers() throws SQLException {
        List<member> members = new ArrayList<>();
        String sql = "SELECT m.*, u.email, u.status as user_status " +
                    "FROM members m " +
                    "JOIN users u ON m.user_id = u.user_id " +
                    "ORDER BY m.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        }
        
        return members;
    }
    
    /**
     * Updates a member
     */
    public boolean updateMember(member member) throws SQLException {
        String sql = "UPDATE members SET first_name = ?, last_name = ?, phone = ?, " +
                    "date_of_birth = ?, gender = ?, address = ?, " +
                    "emergency_contact_name = ?, emergency_contact_phone = ?, " +
                    "membership_type = ?, membership_expiry_date = ?, " +
                    "height_cm = ?, weight_kg = ?, fitness_goal = ?, medical_notes = ? " +
                    "WHERE member_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            pstmt.setString(3, validationUtil.cleanPhoneNumber(member.getPhone()));
            pstmt.setDate(4, member.getDateOfBirth());
            pstmt.setString(5, member.getGender());
            pstmt.setString(6, member.getAddress());
            pstmt.setString(7, member.getEmergencyContactName());
            pstmt.setString(8, validationUtil.cleanPhoneNumber(member.getEmergencyContactPhone()));
            pstmt.setString(9, member.getMembershipType());
            pstmt.setDate(10, member.getMembershipExpiryDate());
            pstmt.setObject(11, member.getHeightCm());
            pstmt.setObject(12, member.getWeightKg());
            pstmt.setString(13, member.getFitnessGoal());
            pstmt.setString(14, member.getMedicalNotes());
            pstmt.setInt(15, member.getMemberId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Checks in a member
     */
    public int checkInMember(int memberId, String checkInMethod) throws SQLException {
        member member = getMemberById(memberId);
        if (member == null || !member.isMembershipActive()) {
            throw new IllegalStateException("Member not found or membership inactive");
        }
        
        String sql = "INSERT INTO attendance (member_id, check_in_method) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, memberId);
            pstmt.setString(2, checkInMethod);
            
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
     * Checks out a member
     */
    public boolean checkOutMember(int attendanceId) throws SQLException {
        String sql = "UPDATE attendance SET check_out_time = NOW() WHERE attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Gets member attendance history
     */
    public List<attendance> getMemberAttendance(int memberId) throws SQLException {
        List<attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(m.first_name, ' ', m.last_name) as member_name " +
                    "FROM attendance a " +
                    "JOIN members m ON a.member_id = m.member_id " +
                    "WHERE a.member_id = ? " +
                    "ORDER BY a.check_in_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendance attendance = new attendance();
                    attendance.setAttendanceId(rs.getInt("attendance_id"));
                    attendance.setMemberId(rs.getInt("member_id"));
                    attendance.setCheckInTime(rs.getTimestamp("check_in_time"));
                    attendance.setCheckOutTime(rs.getTimestamp("check_out_time"));
                    attendance.setCheckInMethod(rs.getString("check_in_method"));
                    attendance.setCreatedAt(rs.getTimestamp("created_at"));
                    attendance.setMemberName(rs.getString("member_name"));
                    attendanceList.add(attendance);
                }
            }
        }
        
        return attendanceList;
    }
    
    /**
     * Deletes a member
     */
    public boolean deleteMember(int memberId) throws SQLException {
        member member = getMemberById(memberId);
        if (member == null) {
            return false;
        }
        
        return userService.deleteUser(member.getUserId());
    }
    
    /**
     * Maps ResultSet to Member object
     */
    private member mapResultSetToMember(ResultSet rs) throws SQLException {
        member member = new member();
        member.setMemberId(rs.getInt("member_id"));
        member.setUserId(rs.getInt("user_id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setPhone(rs.getString("phone"));
        member.setDateOfBirth(rs.getDate("date_of_birth"));
        member.setGender(rs.getString("gender"));
        member.setAddress(rs.getString("address"));
        member.setEmergencyContactName(rs.getString("emergency_contact_name"));
        member.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        member.setMembershipType(rs.getString("membership_type"));
        member.setJoinDate(rs.getDate("join_date"));
        member.setMembershipExpiryDate(rs.getDate("membership_expiry_date"));
        member.setHeightCm(rs.getDouble("height_cm"));
        member.setWeightKg(rs.getDouble("weight_kg"));
        member.setFitnessGoal(rs.getString("fitness_goal"));
        member.setMedicalNotes(rs.getString("medical_notes"));
        member.setCreatedAt(rs.getTimestamp("created_at"));
        member.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        try {
            member.setEmail(rs.getString("email"));
            member.setUserStatus(rs.getString("user_status"));
        } catch (SQLException e) {
            // These fields might not be in all queries
        }
        
        return member;
    }
}