package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Trainer Model Class
 * Represents gym trainers/instructors
 */
public class trainer {
    private int trainerId;
    private int userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialization;
    private int experienceYears;
    private String certification;
    private String bio;
    private String availabilitySchedule;
    private Date hireDate;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String email;
    
    // Constructors
    public trainer() {
    }
    
    public trainer(int userId, String firstName, String lastName, String phone, 
                   String specialization, Date hireDate) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public int getTrainerId() {
        return trainerId;
    }
    
    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public int getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
    
    public String getCertification() {
        return certification;
    }
    
    public void setCertification(String certification) {
        this.certification = certification;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }
    
    public void setAvailabilitySchedule(String availabilitySchedule) {
        this.availabilitySchedule = availabilitySchedule;
    }
    
    public Date getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Utility methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    @Override
    public String toString() {
        return "Trainer{" +
                "trainerId=" + trainerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}