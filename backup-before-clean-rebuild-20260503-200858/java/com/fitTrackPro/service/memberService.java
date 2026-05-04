package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.member;
import com.fitTrackPro.util.dateUtil;
import com.fitTrackPro.util.passwordUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class memberService {
    public int registerMemberAccount(String email, String password, member member) throws SQLException {
        String findUserSql = "SELECT u.id AS user_id, m.member_id FROM users u "
                + "LEFT JOIN members m ON m.user_id = u.id WHERE u.email = ? OR u.username = ?";
        String insertUserSql = "INSERT INTO users (username, password, name, email, role, status) VALUES (?, ?, ?, ?, 'MEMBER', 'ACTIVE')";
        try (Connection connection = DBConnection.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                Integer userId = null;
                try (PreparedStatement findUser = connection.prepareStatement(findUserSql)) {
                    findUser.setString(1, email);
                    findUser.setString(2, email);
                    try (ResultSet rs = findUser.executeQuery()) {
                        if (rs.next()) {
                            int existingMemberId = rs.getInt("member_id");
                            if (!rs.wasNull() && existingMemberId > 0) {
                                throw new SQLException("An account already exists for that email.", "23000");
                            }
                            userId = rs.getInt("user_id");
                        }
                    }
                }

                if (userId == null) {
                    try (PreparedStatement insertUser = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertUser.setString(1, email);
                        insertUser.setString(2, passwordUtil.hashPassword(password));
                        insertUser.executeUpdate();
                        try (ResultSet keys = insertUser.getGeneratedKeys()) {
                            if (keys.next()) {
                                userId = keys.getInt(1);
                            }
                        }
                    }
                }

                if (userId == null) {
                    throw new SQLException("Creating user failed, no generated id returned.");
                }

                member.setUserId(userId);
                int memberId = createMember(connection, member);
                connection.commit();
                return memberId;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public int createMember(member member) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            return createMember(connection, member);
        }
    }

    private int createMember(Connection connection, member member) throws SQLException {
        String sql = "INSERT INTO members (user_id, first_name, last_name, phone, date_of_birth, gender, address, "
                + "emergency_contact_name, emergency_contact_phone, membership_type, join_date, membership_expiry_date, "
                + "height_cm, weight_kg, fitness_goal, medical_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindMember(statement, member);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating member failed, no generated id returned.");
    }

    public boolean updateMember(member member, boolean includeAdminFields) throws SQLException {
        String sql = includeAdminFields
                ? "UPDATE members SET first_name = ?, last_name = ?, phone = ?, date_of_birth = ?, gender = ?, address = ?, "
                        + "emergency_contact_name = ?, emergency_contact_phone = ?, membership_type = ?, membership_expiry_date = ?, "
                        + "height_cm = ?, weight_kg = ?, fitness_goal = ?, medical_notes = ? WHERE member_id = ?"
                : "UPDATE members SET phone = ?, address = ?, emergency_contact_name = ?, emergency_contact_phone = ?, "
                        + "height_cm = ?, weight_kg = ?, fitness_goal = ? WHERE member_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (includeAdminFields) {
                statement.setString(1, member.getFirstName());
                statement.setString(2, member.getLastName());
                statement.setString(3, member.getPhone());
                statement.setDate(4, member.getDateOfBirth());
                statement.setString(5, member.getGender());
                statement.setString(6, member.getAddress());
                statement.setString(7, member.getEmergencyContactName());
                statement.setString(8, member.getEmergencyContactPhone());
                statement.setString(9, member.getMembershipType());
                statement.setDate(10, member.getMembershipExpiryDate());
                statement.setObject(11, member.getHeightCm());
                statement.setObject(12, member.getWeightKg());
                statement.setString(13, member.getFitnessGoal());
                statement.setString(14, member.getMedicalNotes());
                statement.setInt(15, member.getMemberId());
            } else {
                statement.setString(1, member.getPhone());
                statement.setString(2, member.getAddress());
                statement.setString(3, member.getEmergencyContactName());
                statement.setString(4, member.getEmergencyContactPhone());
                statement.setObject(5, member.getHeightCm());
                statement.setObject(6, member.getWeightKg());
                statement.setString(7, member.getFitnessGoal());
                statement.setInt(8, member.getMemberId());
                statement.setInt(9, member.getUserId());
            }
            return statement.executeUpdate() > 0;
        }
    }

    public member findByUserId(int userId) throws SQLException {
        return findOne(baseSelect() + " WHERE m.user_id = ?", userId);
    }

    public member findById(int memberId) throws SQLException {
        return findOne(baseSelect() + " WHERE m.member_id = ?", memberId);
    }

    public List<member> findAll() throws SQLException {
        String sql = baseSelect() + " ORDER BY m.first_name, m.last_name";
        List<member> members = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                members.add(mapMember(rs));
            }
        }
        return members;
    }

    public List<member> findRecentMembers(int limit) throws SQLException {
        String sql = baseSelect() + " ORDER BY m.created_at DESC LIMIT ?";
        List<member> members = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    members.add(mapMember(rs));
                }
            }
        }
        return members;
    }

    public int countAll() throws SQLException {
        return count("SELECT COUNT(*) FROM members");
    }

    public int countActive() throws SQLException {
        return count("SELECT COUNT(*) FROM members WHERE membership_expiry_date >= CURRENT_DATE");
    }

    public int countExpired() throws SQLException {
        return count("SELECT COUNT(*) FROM members WHERE membership_expiry_date < CURRENT_DATE");
    }

    public Date calculateExpiry(String membershipType, Date joinDate) {
        int months = switch (membershipType == null ? "" : membershipType) {
            case "FAMILY" -> 6;
            case "PREMIUM" -> 3;
            default -> 1;
        };
        return dateUtil.addMonths(joinDate, months);
    }

    private member findOne(String sql, int id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? mapMember(rs) : null;
            }
        }
    }

    private int count(String sql) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private String baseSelect() {
        return "SELECT m.*, u.email, u.status AS user_status FROM members m "
                + "JOIN users u ON u.id = m.user_id";
    }

    private void bindMember(PreparedStatement statement, member member) throws SQLException {
        statement.setInt(1, member.getUserId());
        statement.setString(2, member.getFirstName());
        statement.setString(3, member.getLastName());
        statement.setString(4, member.getPhone());
        statement.setDate(5, member.getDateOfBirth());
        statement.setString(6, member.getGender());
        statement.setString(7, member.getAddress());
        statement.setString(8, member.getEmergencyContactName());
        statement.setString(9, member.getEmergencyContactPhone());
        statement.setString(10, member.getMembershipType());
        statement.setDate(11, member.getJoinDate());
        statement.setDate(12, member.getMembershipExpiryDate());
        statement.setObject(13, member.getHeightCm());
        statement.setObject(14, member.getWeightKg());
        statement.setString(15, member.getFitnessGoal());
        statement.setString(16, member.getMedicalNotes());
    }

    private member mapMember(ResultSet rs) throws SQLException {
        member result = new member();
        result.setMemberId(rs.getInt("member_id"));
        result.setUserId(rs.getInt("user_id"));
        result.setFirstName(rs.getString("first_name"));
        result.setLastName(rs.getString("last_name"));
        result.setPhone(rs.getString("phone"));
        result.setDateOfBirth(rs.getDate("date_of_birth"));
        result.setGender(rs.getString("gender"));
        result.setAddress(rs.getString("address"));
        result.setEmergencyContactName(rs.getString("emergency_contact_name"));
        result.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        result.setMembershipType(rs.getString("membership_type"));
        result.setJoinDate(rs.getDate("join_date"));
        result.setMembershipExpiryDate(rs.getDate("membership_expiry_date"));
        result.setHeightCm(toDouble(rs.getObject("height_cm")));
        result.setWeightKg(toDouble(rs.getObject("weight_kg")));
        result.setFitnessGoal(rs.getString("fitness_goal"));
        result.setMedicalNotes(rs.getString("medical_notes"));
        result.setCreatedAt(rs.getTimestamp("created_at"));
        result.setUpdatedAt(rs.getTimestamp("updated_at"));
        result.setEmail(rs.getString("email"));
        result.setUserStatus(rs.getString("user_status"));
        return result;
    }

    private Double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : null;
    }
}