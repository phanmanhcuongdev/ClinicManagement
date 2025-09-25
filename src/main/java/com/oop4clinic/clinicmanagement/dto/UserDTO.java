package com.oop4clinic.clinicmanagement.dto;

import com.oop4clinic.clinicmanagement.model.enums.UserRole;

import java.time.LocalDateTime;

public class UserDTO {
    private int id;
    private String username;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;

    public UserDTO() {}

    public UserDTO(int id, String username, UserRole role, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
