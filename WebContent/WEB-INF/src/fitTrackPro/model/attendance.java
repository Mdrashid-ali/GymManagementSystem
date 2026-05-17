package com.fitTrackPro.model;

import java.sql.Timestamp;

public class attendance {

    private int attendanceId, memberId;
    private Timestamp checkInTime, checkOutTime, createdAt;
    private String checkInMethod, memberName;

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int v) {
        attendanceId = v;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int v) {
        memberId = v;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp v) {
        checkInTime = v;
    }

    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Timestamp v) {
        checkOutTime = v;
    }

    public String getCheckInMethod() {
        return checkInMethod;
    }

    public void setCheckInMethod(String v) {
        checkInMethod = v;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp v) {
        createdAt = v;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String v) {
        memberName = v;
    }

    public boolean isCheckedOut() {
        return checkOutTime != null;
    }

    public long getDurationMinutes() {
        Timestamp end = checkOutTime == null ? new Timestamp(System.currentTimeMillis()) : checkOutTime;
        if (checkInTime == null) {
            return 0;
        }
        long minutes = (end.getTime() - checkInTime.getTime()) / 60000;
        return Math.max(0, minutes);
    }

    public String getFormattedDuration() {
        long m = getDurationMinutes();
        return m / 60 > 0 ? (m / 60) + "h " + (m % 60) + "m" : m + " minutes";
    }
}
