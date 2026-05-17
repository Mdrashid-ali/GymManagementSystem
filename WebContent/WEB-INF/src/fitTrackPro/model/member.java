package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

public class member {

    private int memberId, userId;
    private String firstName, lastName, phone, gender, address, emergencyContactName, emergencyContactPhone, membershipType, fitnessGoal, medicalNotes, email, userStatus;
    private Date dateOfBirth, joinDate, membershipExpiryDate;
    private Double heightCm, weightKg;
    private Timestamp createdAt, updatedAt;

    public member() {}

    public member(String firstName, String lastName, String phone, int userId, String membershipType, Date joinDate, Date membershipExpiryDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userId = userId;
        this.membershipType = membershipType;
        this.joinDate = joinDate;
        this.membershipExpiryDate = membershipExpiryDate;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int v) {
        memberId = v;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        userId = v;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date v) {
        dateOfBirth = v;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String v) {
        gender = v;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String v) {
        address = v;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String v) {
        emergencyContactName = v;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String v) {
        emergencyContactPhone = v;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String v) {
        membershipType = v;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date v) {
        joinDate = v;
    }

    public Date getMembershipExpiryDate() {
        return membershipExpiryDate;
    }

    public void setMembershipExpiryDate(Date v) {
        membershipExpiryDate = v;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double v) {
        heightCm = v;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double v) {
        weightKg = v;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String v) {
        fitnessGoal = v;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String v) {
        medicalNotes = v;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String v) {
        userStatus = v;
    }

    public boolean isMembershipActive() {
        return membershipExpiryDate != null && !membershipExpiryDate.before(new Date(System.currentTimeMillis()));
    }
}