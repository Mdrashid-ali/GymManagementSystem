package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a workout plan assigned by a trainer to a member.
 */
public class workoutPlan {

	private int planId, trainerId, memberId;
	private String planName, description, status, exercises, notes, trainerName, memberName;
	private Date startDate, endDate;
	private Timestamp createdAt, updatedAt;

	public workoutPlan() {
	}

	public workoutPlan(int trainerId, int memberId, String planName, Date startDate, Date endDate) {
		this.trainerId = trainerId;
		this.memberId = memberId;
		this.planName = planName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = "ACTIVE";
	}

	/** Gets the required value. */
	public int getPlanId() {
		return planId;
	}

	/** Sets the required value. */
	public void setPlanId(int v) {
		planId = v;
	}

	/** Gets the required value. */
	public int getTrainerId() {
		return trainerId;
	}

	/** Sets the required value. */
	public void setTrainerId(int v) {
		trainerId = v;
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
	public String getPlanName() {
		return planName;
	}

	/** Sets the required value. */
	public void setPlanName(String v) {
		planName = v;
	}

	/** Gets the required value. */
	public String getDescription() {
		return description;
	}

	/** Sets the required value. */
	public void setDescription(String v) {
		description = v;
	}

	/** Gets the required value. */
	public Date getStartDate() {
		return startDate;
	}

	/** Sets the required value. */
	public void setStartDate(Date v) {
		startDate = v;
	}

	/** Gets the required value. */
	public Date getEndDate() {
		return endDate;
	}

	/** Sets the required value. */
	public void setEndDate(Date v) {
		endDate = v;
	}

	/** Gets the required value. */
	public String getStatus() {
		return status;
	}

	/** Sets the required value. */
	public void setStatus(String v) {
		status = v;
	}

	/** Gets the required value. */
	public String getExercises() {
		return exercises;
	}

	/** Sets the required value. */
	public void setExercises(String v) {
		exercises = v;
	}

	/** Gets the required value. */
	public String getNotes() {
		return notes;
	}

	/** Sets the required value. */
	public void setNotes(String v) {
		notes = v;
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
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	/** Sets the required value. */
	public void setUpdatedAt(Timestamp v) {
		updatedAt = v;
	}

	/** Gets the required value. */
	public String getTrainerName() {
		return trainerName;
	}

	/** Sets the required value. */
	public void setTrainerName(String v) {
		trainerName = v;
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
	public boolean isActive() {
		return "ACTIVE".equalsIgnoreCase(status);
	}

	/** Checks the required condition. */
	public boolean isCompleted() {
		return "COMPLETED".equalsIgnoreCase(status);
	}
}
