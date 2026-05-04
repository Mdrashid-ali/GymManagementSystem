package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * WorkoutPlan Model Class
 * Represents workout plans assigned to members by trainers
 */
public class workoutPlan {
    private int planId;
    private int trainerId;
    private int memberId;
    private String planName;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status;
    private String exercises;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String trainerName;
    private String memberName;
    
    // Constructors
    public workoutPlan() {
    }
    
    public workoutPlan(int trainerId, int memberId, String planName, 
                       Date startDate, Date endDate) {
        this.trainerId = trainerId;
        this.memberId = memberId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public int getPlanId() {
        return planId;
    }
    
    public void setPlanId(int planId) {
        this.planId = planId;
    }
    
    public int getTrainerId() {
        return trainerId;
    }
    
    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getExercises() {
        return exercises;
    }
    
    public void setExercises(String exercises) {
        this.exercises = exercises;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getTrainerName() {
        return trainerName;
    }
    
    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    // Utility methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        Date today = new Date(System.currentTimeMillis());
        return endDate.before(today);
    }
    
    @Override
    public String toString() {
        return "WorkoutPlan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", memberName='" + memberName + '\'' +
                ", trainerName='" + trainerName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}