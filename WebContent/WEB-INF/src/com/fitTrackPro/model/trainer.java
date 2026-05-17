package com.fitTrackPro.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a trainer profile and professional details.
 */
public class trainer {

	private int trainerId, userId, experienceYears;
	private String firstName, lastName, phone, specialization, certification, bio, availabilitySchedule, status, email;
	private Date hireDate;
	private Timestamp createdAt, updatedAt;

	/** Gets the required value. */
	public int getTrainerId() {
		return trainerId;
	}

	/** Sets the required value. */
	public void setTrainerId(int v) {
		trainerId = v;
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
	public int getExperienceYears() {
		return experienceYears;
	}

	/** Sets the required value. */
	public void setExperienceYears(int v) {
		experienceYears = v;
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
	public String getSpecialization() {
		return specialization;
	}

	/** Sets the required value. */
	public void setSpecialization(String v) {
		specialization = v;
	}

	/** Gets the required value. */
	public String getCertification() {
		return certification;
	}

	/** Sets the required value. */
	public void setCertification(String v) {
		certification = v;
	}

	/** Gets the required value. */
	public String getBio() {
		return bio;
	}

	/** Sets the required value. */
	public void setBio(String v) {
		bio = v;
	}

	/** Gets the required value. */
	public String getAvailabilitySchedule() {
		return availabilitySchedule;
	}

	/** Sets the required value. */
	public void setAvailabilitySchedule(String v) {
		availabilitySchedule = v;
	}

	/** Gets the required value. */
	public Date getHireDate() {
		return hireDate;
	}

	/** Sets the required value. */
	public void setHireDate(Date v) {
		hireDate = v;
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
}
