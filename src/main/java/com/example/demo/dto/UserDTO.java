package com.example.demo.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int totalPoints;
    private int quizCompleted;
    private int postsCount;

    public UserDTO() {}

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getQuizCompleted() { return quizCompleted; }
    public void setQuizCompleted(int quizCompleted) { this.quizCompleted = quizCompleted; }

    public int getPostsCount() { return postsCount; }
    public void setPostsCount(int postsCount) { this.postsCount = postsCount; }
}