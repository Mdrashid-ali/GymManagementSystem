package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

public class trainer {

    private int trainerId, userId, experienceYears;
    private String firstName, lastName, phone, specialization, certification, bio, availabilitySchedule, status, email;
    private Date hireDate;
    private Timestamp createdAt, updatedAt;

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int v) {
        trainerId = v;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        userId = v;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int v) {
        experienceYears = v;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String v) {
        firstName = v;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String v) {
        lastName = v;
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        phone = v;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String v) {
        specialization = v;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String v) {
        certification = v;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String v) {
        bio = v;
    }

    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }

    public void setAvailabilitySchedule(String v) {
        availabilitySchedule = v;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date v) {
        hireDate = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }
}