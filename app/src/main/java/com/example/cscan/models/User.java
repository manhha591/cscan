package com.example.cscan.models;

import java.util.ArrayList;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String NewPassword;

    public User(String username, String password, String newPassword) {
        this.username = username;
        this.password = password;
        NewPassword = newPassword;
    }

    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<GroupImage> groupImageArrayList;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ArrayList<GroupImage> getGroupImages() {
        return groupImageArrayList;
    }

    public void setGroupImages(ArrayList<GroupImage> groupImageArrayList) {
        this.groupImageArrayList = groupImageArrayList;
    }

    public User() {
    }

    public User(int userid, String username, String password, String email, String phoneNumber) {
        this.userId = userid;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
