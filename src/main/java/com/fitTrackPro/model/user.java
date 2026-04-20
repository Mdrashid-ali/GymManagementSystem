package com.fitTrackPro.model;

import java.sql.Timestamp;

/**
 * User Model Class
 * Represents system users with authentication details
 */
public class user {
    private int userId;
    private String email;
    private String passwordHash;
    private String role;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public user() {
    }
    
    public user(String email, String passwordHash, String role, String status) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }
    
    public user(int userId, String email, String passwordHash, String role, String status) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    // Utility methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isLocked() {
        return "LOCKED".equals(status);
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    public boolean isTrainer() {
        return "TRAINER".equals(role);
    }
    
    public boolean isMember() {
        return "MEMBER".equals(role);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}