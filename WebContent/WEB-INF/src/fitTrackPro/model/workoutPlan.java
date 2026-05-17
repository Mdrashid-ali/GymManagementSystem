package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

public class workoutPlan {

    private int planId, trainerId, memberId;
    private String planName, description, status, exercises, notes, trainerName, memberName;
    private Date startDate, endDate;
    private Timestamp createdAt, updatedAt;

    public workoutPlan() {}

    public workoutPlan(int trainerId, int memberId, String planName, Date startDate, Date endDate) {
        this.trainerId = trainerId;
        this.memberId = memberId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "ACTIVE";
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int v) {
        planId = v;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int v) {
        trainerId = v;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int v) {
        memberId = v;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String v) {
        planName = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date v) {
        startDate = v;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date v) {
        endDate = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
    }

    public String getExercises() {
        return exercises;
    }

    public void setExercises(String v) {
        exercises = v;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String v) {
        notes = v;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp v) {
        createdAt = v;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp v) {
        updatedAt = v;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String v) {
        trainerName = v;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String v) {
        memberName = v;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
}