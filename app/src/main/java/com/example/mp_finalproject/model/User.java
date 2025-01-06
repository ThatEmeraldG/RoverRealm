package com.example.mp_finalproject.model;

import java.util.List;

public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String profilePicture;

    public User(String userId, String username, String email, String password, String profilePicture) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
