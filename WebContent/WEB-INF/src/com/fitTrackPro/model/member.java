package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a gym member profile and membership state.
 */
public class member {

	private int memberId, userId;
	private String firstName, lastName, phone, gender, address, emergencyContactName, emergencyContactPhone,
			membershipType, fitnessGoal, medicalNotes, email, userStatus;
	private Date dateOfBirth, joinDate, membershipExpiryDate;
	private Double heightCm, weightKg;
	private Timestamp createdAt, updatedAt;

	public member() {
	}

	public member(String firstName, String lastName, String phone, int userId, String membershipType, Date joinDate,
			Date membershipExpiryDate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.userId = userId;
		this.membershipType = membershipType;
		this.joinDate = joinDate;
		this.membershipExpiryDate = membershipExpiryDate;
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
	public int getUserId() {
		return userId;
	}

	/** Sets the required value. */
	public void setUserId(int v) {
		userId = v;
	}

	/** Gets the required value. */
	public String getFirstName() {
		return firstName;
	}

	/** Sets the required value. */
	public void setFirstName(String v) {
		firstName = v;
	}

	/** Gets the required value. */
	public String getLastName() {
		return lastName;
	}

	/** Sets the required value. */
	public void setLastName(String v) {
		lastName = v;
	}

	/** Gets the required value. */
	public String getFullName() {
		return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
	}

	/** Gets the required value. */
	public String getPhone() {
		return phone;
	}

	/** Sets the required value. */
	public void setPhone(String v) {
		phone = v;
	}

	/** Gets the required value. */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/** Sets the required value. */
	public void setDateOfBirth(Date v) {
		dateOfBirth = v;
	}

	/** Gets the required value. */
	public String getGender() {
		return gender;
	}

	/** Sets the required value. */
	public void setGender(String v) {
		gender = v;
	}

	/** Gets the required value. */
	public String getAddress() {
		return address;
	}

	/** Sets the required value. */
	public void setAddress(String v) {
		address = v;
	}

	/** Gets the required value. */
	public String getEmergencyContactName() {
		return emergencyContactName;
	}

	/** Sets the required value. */
	public void setEmergencyContactName(String v) {
		emergencyContactName = v;
	}

	/** Gets the required value. */
	public String getEmergencyContactPhone() {
		return emergencyContactPhone;
	}

	/** Sets the required value. */
	public void setEmergencyContactPhone(String v) {
		emergencyContactPhone = v;
	}

	/** Gets the required value. */
	public String getMembershipType() {
		return membershipType;
	}

	/** Sets the required value. */
	public void setMembershipType(String v) {
		membershipType = v;
	}

	/** Gets the required value. */
	public Date getJoinDate() {
		return joinDate;
	}

	/** Sets the required value. */
	public void setJoinDate(Date v) {
		joinDate = v;
	}

	/** Gets the required value. */
	public Date getMembershipExpiryDate() {
		return membershipExpiryDate;
	}

	/** Sets the required value. */
	public void setMembershipExpiryDate(Date v) {
		membershipExpiryDate = v;
	}

	/** Gets the required value. */
	public Double getHeightCm() {
		return heightCm;
	}

	/** Sets the required value. */
	public void setHeightCm(Double v) {
		heightCm = v;
	}

	/** Gets the required value. */
	public Double getWeightKg() {
		return weightKg;
	}

	/** Sets the required value. */
	public void setWeightKg(Double v) {
		weightKg = v;
	}

	/** Gets the required value. */
	public String getFitnessGoal() {
		return fitnessGoal;
	}

	/** Sets the required value. */
	public void setFitnessGoal(String v) {
		fitnessGoal = v;
	}

	/** Gets the required value. */
	public String getMedicalNotes() {
		return medicalNotes;
	}

	/** Sets the required value. */
	public void setMedicalNotes(String v) {
		medicalNotes = v;
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
	public String getEmail() {
		return email;
	}

	/** Sets the required value. */
	public void setEmail(String v) {
		email = v;
	}

	/** Gets the required value. */
	public String getUserStatus() {
		return userStatus;
	}

	/** Sets the required value. */
	public void setUserStatus(String v) {
		userStatus = v;
	}

	/** Checks the required condition. */
	public boolean isMembershipActive() {
		return membershipExpiryDate != null && !membershipExpiryDate.before(new Date(System.currentTimeMillis()));
	}
}
