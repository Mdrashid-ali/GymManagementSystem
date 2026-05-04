package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Member Model Class
 * Extends user information with member-specific details
 */
public class member {
    private int memberId;
    private int userId;
    private String firstName;
    private String lastName;
    private String phone;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String membershipType;
    private Date joinDate;
    private Date membershipExpiryDate;
    private Double heightCm;
    private Double weightKg;
    private String fitnessGoal;
    private String medicalNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String email;
    private String userStatus;
    
    // Constructors
    public member() {
    }
    
    public member(String firstName, String lastName, String phone, int userId, 
                  String membershipType, Date joinDate, Date membershipExpiryDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userId = userId;
        this.membershipType = membershipType;
        this.joinDate = joinDate;
        this.membershipExpiryDate = membershipExpiryDate;
    }
    
    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
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
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getMembershipType() {
        return membershipType;
    }
    
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
    
    public Date getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
    
    public Date getMembershipExpiryDate() {
        return membershipExpiryDate;
    }
    
    public void setMembershipExpiryDate(Date membershipExpiryDate) {
        this.membershipExpiryDate = membershipExpiryDate;
    }
    
    public Double getHeightCm() {
        return heightCm;
    }
    
    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }
    
    public Double getWeightKg() {
        return weightKg;
    }
    
    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }
    
    public String getFitnessGoal() {
        return fitnessGoal;
    }
    
    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }
    
    public String getMedicalNotes() {
        return medicalNotes;
    }
    
    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
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
    
    public String getUserStatus() {
        return userStatus;
    }
    
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
    
    // Utility methods
    public boolean isMembershipActive() {
        if (membershipExpiryDate == null) {
            return false;
        }
        Date today = new Date(System.currentTimeMillis());
        return !membershipExpiryDate.before(today);
    }
    
    public Double calculateBMI() {
        if (heightCm == null || weightKg == null || heightCm == 0) {
            return null;
        }
        double heightInMeters = heightCm / 100.0;
        return weightKg / (heightInMeters * heightInMeters);
    }
    
    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", membershipType='" + membershipType + '\'' +
                '}';
    }
}