package com.fitTrackPro.model;

import java.sql.Timestamp;

/**
 * Represents an authenticated application user and role information.
 */
public class user {
	private int userId;
	private String email;
	private String name;
	private String passwordHash;
	private String role;
	private String status;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public user() {
	}

	public user(int userId, String email, String passwordHash, String role, String status) {
		this.userId = userId;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return name == null || name.isBlank() ? email : name;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	public boolean isActive() {
		return "ACTIVE".equalsIgnoreCase(status);
	}

	public boolean isAdmin() {
		return "ADMIN".equalsIgnoreCase(role);
	}

	public boolean isTrainer() {
		return "TRAINER".equalsIgnoreCase(role);
	}

	public boolean isMember() {
		return "MEMBER".equalsIgnoreCase(role);
	}
}
