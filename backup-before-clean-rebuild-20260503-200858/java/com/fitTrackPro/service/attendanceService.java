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

public class attendanceService {
    public List<attendance> findByMemberId(int memberId) throws SQLException {
        String sql = "SELECT a.*, CONCAT(m.first_name, ' ', m.last_name) AS member_name "
                + "FROM attendance a JOIN members m ON m.member_id = a.member_id "
                + "WHERE a.member_id = ? ORDER BY a.check_in_time DESC";
        List<attendance> records = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    records.add(mapAttendance(rs));
                }
            }
        }
        return records;
    }

    public void checkIn(int memberId) throws SQLException {
        String sql = "INSERT INTO attendance (member_id, check_in_method) VALUES (?, 'WEB')";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberId);
            statement.executeUpdate();
        }
    }

    private attendance mapAttendance(ResultSet rs) throws SQLException {
        attendance result = new attendance();
        result.setAttendanceId(rs.getInt("attendance_id"));
        result.setMemberId(rs.getInt("member_id"));
        result.setCheckInTime(rs.getTimestamp("check_in_time"));
        result.setCheckOutTime(rs.getTimestamp("check_out_time"));
        result.setCheckInMethod(rs.getString("check_in_method"));
        result.setCreatedAt(rs.getTimestamp("created_at"));
        result.setMemberName(rs.getString("member_name"));
        return result;
    }
}
