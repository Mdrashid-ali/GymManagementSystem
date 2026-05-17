package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.fitnessDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs member fitness progress database operations.
 */
public class fitnessDetailService {
	/** Adds a new fitness progress entry. */
	public int create(fitnessDetail detail) throws SQLException {
		String sql = "INSERT INTO member_fitness_details (member_id, height_cm, weight_kg, body_fat_percent, muscle_mass_kg, fitness_goal, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, detail.getMemberId());
			ps.setObject(2, detail.getHeightCm());
			ps.setObject(3, detail.getWeightKg());
			ps.setObject(4, detail.getBodyFatPercent());
			ps.setObject(5, detail.getMuscleMassKg());
			ps.setString(6, detail.getFitnessGoal());
			ps.setString(7, detail.getNotes());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next())
					return keys.getInt(1);
			}
		}
		throw new SQLException("Creating fitness detail failed.");
	}

	/** Finds all fitness entries for one member, newest first. */
	public List<fitnessDetail> findByMemberId(int memberId) throws SQLException {
		String sql = "SELECT * FROM member_fitness_details WHERE member_id = ? ORDER BY recorded_at DESC, detail_id DESC";
		List<fitnessDetail> details = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					details.add(map(rs));
			}
		}
		return details;
	}

	/** Finds the latest fitness entry for one member. */
	public fitnessDetail findLatestByMemberId(int memberId) throws SQLException {
		String sql = "SELECT * FROM member_fitness_details WHERE member_id = ? ORDER BY recorded_at DESC, detail_id DESC LIMIT 1";
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	/** Maps one result row into a model object. */
	private fitnessDetail map(ResultSet rs) throws SQLException {
		fitnessDetail detail = new fitnessDetail();
		detail.setDetailId(rs.getInt("detail_id"));
		detail.setMemberId(rs.getInt("member_id"));
		detail.setHeightCm(toDouble(rs.getObject("height_cm")));
		detail.setWeightKg(toDouble(rs.getObject("weight_kg")));
		detail.setBodyFatPercent(toDouble(rs.getObject("body_fat_percent")));
		detail.setMuscleMassKg(toDouble(rs.getObject("muscle_mass_kg")));
		detail.setFitnessGoal(rs.getString("fitness_goal"));
		detail.setNotes(rs.getString("notes"));
		detail.setRecordedAt(rs.getTimestamp("recorded_at"));
		return detail;
	}

	/** Converts numeric SQL values into Double. */
	private Double toDouble(Object value) {
		return value instanceof Number number ? number.doubleValue() : null;
	}
}
