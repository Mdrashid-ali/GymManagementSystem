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

/**
 * Performs member account, profile, and membership database operations.
 */
public class memberService {
	/** Handles registerMemberAccount logic. */
	public int registerMemberAccount(String email, String password, member member) throws SQLException {
		String findUser = "SELECT u.id AS user_id, m.member_id FROM users u LEFT JOIN members m ON m.user_id = u.id WHERE u.email = ? OR u.username = ?";
		String insertUser = "INSERT INTO users (username, password, name, email, role, status) VALUES (?, ?, ?, ?, 'MEMBER', 'ACTIVE')";

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
							if (rs.getInt("member_id") > 0) {
								throw new SQLException("An account already exists for that email.", "23000");
							}
							userId = rs.getInt("user_id");
						}
					}
				}

				if (userId == null) {
					try (PreparedStatement ps = connection.prepareStatement(insertUser,
							Statement.RETURN_GENERATED_KEYS)) {
						ps.setString(1, email);
						ps.setString(2, passwordUtil.hashPassword(password));
						ps.setString(3, member.getFullName().trim());
						ps.setString(4, email);
						ps.executeUpdate();
						try (ResultSet keys = ps.getGeneratedKeys()) {
							if (keys.next())
								userId = keys.getInt(1);
						}
					}
				}

				if (userId == null)
					throw new SQLException("Creating user failed.");
				member.setUserId(userId);
				int memberId = createMember(connection, member);
				connection.commit();
				return memberId;
			} catch (SQLException | RuntimeException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(oldAutoCommit);
			}
		}
	}

	/** Handles createMember logic. */
	public int createMember(member member) throws SQLException {
		try (Connection connection = DBConnection.getConnection()) {
			return createMember(connection, member);
		}
	}

	/** Handles createMember logic. */
	private int createMember(Connection connection, member member) throws SQLException {
		String sql = "INSERT INTO members (user_id, first_name, last_name, phone, date_of_birth, gender, address, emergency_contact_name, emergency_contact_phone, membership_type, join_date, membership_expiry_date, height_cm, weight_kg, fitness_goal, medical_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			bindMember(ps, member);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next())
					return keys.getInt(1);
			}
		}
		throw new SQLException("Creating member failed.");
	}

	/** Handles updateMember logic. */
	public boolean updateMember(member member, boolean admin) throws SQLException {
		String sql = admin
				? "UPDATE members SET first_name=?, last_name=?, phone=?, date_of_birth=?, gender=?, address=?, emergency_contact_name=?, emergency_contact_phone=?, membership_type=?, membership_expiry_date=?, height_cm=?, weight_kg=?, fitness_goal=?, medical_notes=? WHERE member_id=?"
				: "UPDATE members SET phone=?, address=?, emergency_contact_name=?, emergency_contact_phone=?, height_cm=?, weight_kg=?, fitness_goal=? WHERE member_id=? AND user_id=?";

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			if (admin) {
				ps.setString(1, member.getFirstName());
				ps.setString(2, member.getLastName());
				ps.setString(3, member.getPhone());
				ps.setDate(4, member.getDateOfBirth());
				ps.setString(5, member.getGender());
				ps.setString(6, member.getAddress());
				ps.setString(7, member.getEmergencyContactName());
				ps.setString(8, member.getEmergencyContactPhone());
				ps.setString(9, member.getMembershipType());
				ps.setDate(10, member.getMembershipExpiryDate());
				ps.setObject(11, member.getHeightCm());
				ps.setObject(12, member.getWeightKg());
				ps.setString(13, member.getFitnessGoal());
				ps.setString(14, member.getMedicalNotes());
				ps.setInt(15, member.getMemberId());
			} else {
				ps.setString(1, member.getPhone());
				ps.setString(2, member.getAddress());
				ps.setString(3, member.getEmergencyContactName());
				ps.setString(4, member.getEmergencyContactPhone());
				ps.setObject(5, member.getHeightCm());
				ps.setObject(6, member.getWeightKg());
				ps.setString(7, member.getFitnessGoal());
				ps.setInt(8, member.getMemberId());
				ps.setInt(9, member.getUserId());
			}
			return ps.executeUpdate() > 0;
		}
	}

	/** Handles deleteMember logic. */
	public boolean deleteMember(int memberId) throws SQLException {
		String findUser = "SELECT user_id FROM members WHERE member_id = ?";
		try (Connection connection = DBConnection.getConnection()) {
			boolean oldAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			try {
				Integer userId = null;
				try (PreparedStatement ps = connection.prepareStatement(findUser)) {
					ps.setInt(1, memberId);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next())
							userId = rs.getInt("user_id");
					}
				}

				if (userId == null) {
					connection.rollback();
					return false;
				}

				executeDelete(connection, "DELETE FROM attendance WHERE member_id = ?", memberId);
				executeDelete(connection, "DELETE FROM member_fitness_details WHERE member_id = ?", memberId);
				executeDelete(connection, "DELETE FROM workout_plans WHERE member_id = ?", memberId);
				int deletedMembers = executeDelete(connection, "DELETE FROM members WHERE member_id = ?", memberId);
				executeDelete(connection, "DELETE FROM users WHERE id = ? AND role = 'MEMBER'", userId);
				connection.commit();
				return deletedMembers > 0;
			} catch (SQLException | RuntimeException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(oldAutoCommit);
			}
		}
	}

	/** Handles findByUserId logic. */
	public member findByUserId(int id) throws SQLException {
		return findOne(baseSelect() + " WHERE m.user_id=?", id);
	}

	/** Handles findById logic. */
	public member findById(int id) throws SQLException {
		return findOne(baseSelect() + " WHERE m.member_id=?", id);
	}

	/** Handles findAll logic. */
	public List<member> findAll() throws SQLException {
		return findMany(baseSelect() + " ORDER BY m.first_name, m.last_name", 0);
	}

	/** Handles findByTrainerId logic. */
	public List<member> findByTrainerId(int trainerId) throws SQLException {
		return findMany(baseSelect()
				+ " WHERE m.member_id IN (SELECT DISTINCT member_id FROM workout_plans WHERE trainer_id = ?) ORDER BY m.first_name, m.last_name",
				trainerId);
	}

	/** Handles findRecentMembers logic. */
	public List<member> findRecentMembers(int limit) throws SQLException {
		return findMany(baseSelect() + " ORDER BY m.created_at DESC LIMIT ?", limit);
	}

	/** Handles countAll logic. */
	public int countAll() throws SQLException {
		return count("SELECT COUNT(*) FROM members");
	}

	/** Handles countActive logic. */
	public int countActive() throws SQLException {
		return count("SELECT COUNT(*) FROM members WHERE membership_expiry_date >= CURRENT_DATE");
	}

	/** Handles countExpired logic. */
	public int countExpired() throws SQLException {
		return count("SELECT COUNT(*) FROM members WHERE membership_expiry_date < CURRENT_DATE");
	}

	/** Handles calculateExpiry logic. */
	public Date calculateExpiry(String type, Date joinDate) {
		return dateUtil.addMonths(joinDate, switch (type == null ? "" : type.toUpperCase()) {
		case "FAMILY" -> 6;
		case "PREMIUM" -> 3;
		default -> 1;
		});
	}

	/** Handles executeDelete logic. */
	private int executeDelete(Connection connection, String sql, int id) throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate();
		}
	}

	/** Handles findOne logic. */
	private member findOne(String sql, int id) throws SQLException {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapMember(rs) : null;
			}
		}
	}

	/** Handles findMany logic. */
	private List<member> findMany(String sql, int limit) throws SQLException {
		List<member> members = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			if (limit > 0)
				ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					members.add(mapMember(rs));
			}
		}
		return members;
	}

	/** Handles count logic. */
	private int count(String sql) throws SQLException {
		try (Connection connection = DBConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql)) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	/** Handles baseSelect logic. */
	private String baseSelect() {
		return "SELECT m.*, COALESCE(u.email, u.username) AS email, u.status AS user_status FROM members m JOIN users u ON u.id = m.user_id";
	}

	/** Handles bindMember logic. */
	private void bindMember(PreparedStatement ps, member member) throws SQLException {
		ps.setInt(1, member.getUserId());
		ps.setString(2, member.getFirstName());
		ps.setString(3, member.getLastName());
		ps.setString(4, member.getPhone());
		ps.setDate(5, member.getDateOfBirth());
		ps.setString(6, member.getGender());
		ps.setString(7, member.getAddress());
		ps.setString(8, member.getEmergencyContactName());
		ps.setString(9, member.getEmergencyContactPhone());
		ps.setString(10, member.getMembershipType());
		ps.setDate(11, member.getJoinDate());
		ps.setDate(12, member.getMembershipExpiryDate());
		ps.setObject(13, member.getHeightCm());
		ps.setObject(14, member.getWeightKg());
		ps.setString(15, member.getFitnessGoal());
		ps.setString(16, member.getMedicalNotes());
	}

	/** Handles mapMember logic. */
	private member mapMember(ResultSet rs) throws SQLException {
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
		member.setHeightCm(toDouble(rs.getObject("height_cm")));
		member.setWeightKg(toDouble(rs.getObject("weight_kg")));
		member.setFitnessGoal(rs.getString("fitness_goal"));
		member.setMedicalNotes(rs.getString("medical_notes"));
		member.setCreatedAt(rs.getTimestamp("created_at"));
		member.setUpdatedAt(rs.getTimestamp("updated_at"));
		member.setEmail(rs.getString("email"));
		member.setUserStatus(rs.getString("user_status"));
		return member;
	}

	/** Converts the value. */
	private Double toDouble(Object value) {
		return value instanceof Number number ? number.doubleValue() : null;
	}
}
