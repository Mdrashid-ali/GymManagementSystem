package com.fitTrackPro.service;

import com.fitTrackPro.config.DBConnection;
import com.fitTrackPro.model.attendance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs attendance database operations for members and trainers.
 */
public class attendanceService {
	/** Handles findByMemberId logic. */
	public List<attendance> findByMemberId(int memberId) throws SQLException {
		String sql = baseSelect() + " WHERE a.member_id = ? ORDER BY a.check_in_time DESC";
		List<attendance> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapAttendance(rs));
			}
		}
		return list;
	}

	/** Handles findAll logic. */
	public List<attendance> findAll() throws SQLException {
		List<attendance> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(baseSelect() + " ORDER BY a.check_in_time DESC")) {
			while (rs.next())
				list.add(mapAttendance(rs));
		}
		return list;
	}

	/** Handles findByTrainerId logic. */
	public List<attendance> findByTrainerId(int trainerId, Integer memberId) throws SQLException {
		String sql = baseSelect()
				+ " WHERE a.member_id IN (SELECT DISTINCT member_id FROM workout_plans WHERE trainer_id = ?)";
		if (memberId != null)
			sql += " AND a.member_id = ?";
		sql += " ORDER BY a.check_in_time DESC";
		List<attendance> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, trainerId);
			if (memberId != null)
				ps.setInt(2, memberId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapAttendance(rs));
			}
		}
		return list;
	}

	/** Handles checkIn logic. */
	public void checkIn(int memberId) throws SQLException {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection
						.prepareStatement("INSERT INTO attendance (member_id, check_in_method) VALUES (?, 'WEB')")) {
			ps.setInt(1, memberId);
			ps.executeUpdate();
		}
	}

	/** Handles checkOut logic. */
	public boolean checkOut(int attendanceId) throws SQLException {
		String sql = "UPDATE attendance SET check_out_time = CURRENT_TIMESTAMP WHERE attendance_id = ? AND check_out_time IS NULL";
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, attendanceId);
			return ps.executeUpdate() > 0;
		}
	}

	/** Handles checkOutForTrainer logic. */
	public boolean checkOutForTrainer(int attendanceId, int trainerId) throws SQLException {
		String sql = "UPDATE attendance SET check_out_time = CURRENT_TIMESTAMP WHERE attendance_id = ? AND check_out_time IS NULL AND member_id IN (SELECT DISTINCT member_id FROM workout_plans WHERE trainer_id = ?)";
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, attendanceId);
			ps.setInt(2, trainerId);
			return ps.executeUpdate() > 0;
		}
	}

	/** Handles baseSelect logic. */
	private String baseSelect() {
		return "SELECT a.*, CONCAT(m.first_name, ' ', m.last_name) AS member_name FROM attendance a JOIN members m ON m.member_id = a.member_id";
	}

	/** Handles mapAttendance logic. */
	private attendance mapAttendance(ResultSet rs) throws SQLException {
		attendance attendance = new attendance();
		attendance.setAttendanceId(rs.getInt("attendance_id"));
		attendance.setMemberId(rs.getInt("member_id"));
		attendance.setCheckInTime(rs.getTimestamp("check_in_time"));
		attendance.setCheckOutTime(rs.getTimestamp("check_out_time"));
		attendance.setCheckInMethod(rs.getString("check_in_method"));
		attendance.setCreatedAt(rs.getTimestamp("created_at"));
		attendance.setMemberName(rs.getString("member_name"));
		return attendance;
	}
}
