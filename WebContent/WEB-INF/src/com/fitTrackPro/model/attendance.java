package com.fitTrackPro.model;

import java.sql.Timestamp;

/**
 * Represents a member attendance check-in/check-out record.
 */
public class attendance {

	private int attendanceId, memberId;
	private Timestamp checkInTime, checkOutTime, createdAt;
	private String checkInMethod, memberName;

	/** Gets the required value. */
	public int getAttendanceId() {
		return attendanceId;
	}

	/** Sets the required value. */
	public void setAttendanceId(int v) {
		attendanceId = v;
	}

	/** Gets the required value. */
	public int getMemberId() {
		return memberId;
	}

	/** Sets the required value. */
	public void setMemberId(int v) {
		memberId = v;
	}

	/** Gets the required value. */
	public Timestamp getCheckInTime() {
		return checkInTime;
	}

	/** Sets the required value. */
	public void setCheckInTime(Timestamp v) {
		checkInTime = v;
	}

	/** Gets the required value. */
	public Timestamp getCheckOutTime() {
		return checkOutTime;
	}

	/** Sets the required value. */
	public void setCheckOutTime(Timestamp v) {
		checkOutTime = v;
	}

	/** Gets the required value. */
	public String getCheckInMethod() {
		return checkInMethod;
	}

	/** Sets the required value. */
	public void setCheckInMethod(String v) {
		checkInMethod = v;
	}

	/** Gets the required value. */
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	/** Sets the required value. */
	public void setCreatedAt(Timestamp v) {
		createdAt = v;
	}

	/** Gets the required value. */
	public String getMemberName() {
		return memberName;
	}

	/** Sets the required value. */
	public void setMemberName(String v) {
		memberName = v;
	}

	/** Checks the required condition. */
	public boolean isCheckedOut() {
		return checkOutTime != null;
	}

	/** Gets the required value. */
	public long getDurationMinutes() {
		Timestamp end = checkOutTime == null ? new Timestamp(System.currentTimeMillis()) : checkOutTime;
		if (checkInTime == null) {
			return 0;
		}
		long minutes = (end.getTime() - checkInTime.getTime()) / 60000;
		return Math.max(0, minutes);
	}

	/** Gets the required value. */
	public String getFormattedDuration() {
		long m = getDurationMinutes();
		return m / 60 > 0 ? (m / 60) + "h " + (m % 60) + "m" : m + " minutes";
	}
}
