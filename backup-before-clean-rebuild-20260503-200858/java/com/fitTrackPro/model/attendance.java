package com.fitTrackPro.model;

import java.sql.Timestamp;

/**
 * Attendance Model Class
 * Tracks member gym check-ins and check-outs
 */
public class attendance {
    private int attendanceId;
    private int memberId;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private String checkInMethod;
    private Timestamp createdAt;
    
    // Additional fields for display
    private String memberName;
    
    // Constructors
    public attendance() {
    }
    
    public attendance(int memberId, String checkInMethod) {
        this.memberId = memberId;
        this.checkInMethod = checkInMethod;
        this.checkInTime = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }
    
    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public Timestamp getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(Timestamp checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    
    public String getCheckInMethod() {
        return checkInMethod;
    }
    
    public void setCheckInMethod(String checkInMethod) {
        this.checkInMethod = checkInMethod;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    // Utility methods
    public boolean isCheckedOut() {
        return checkOutTime != null;
    }
    
    public long getDurationMinutes() {
        if (checkOutTime == null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            return (now.getTime() - checkInTime.getTime()) / (1000 * 60);
        }
        return (checkOutTime.getTime() - checkInTime.getTime()) / (1000 * 60);
    }
    
    public String getFormattedDuration() {
        long minutes = getDurationMinutes();
        long hours = minutes / 60;
        long mins = minutes % 60;
        
        if (hours > 0) {
            return hours + "h " + mins + "m";
        }
        return mins + " minutes";
    }
    
    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", memberName='" + memberName + '\'' +
                ", checkInTime=" + checkInTime +
                ", isCheckedOut=" + isCheckedOut() +
                '}';
    }
}