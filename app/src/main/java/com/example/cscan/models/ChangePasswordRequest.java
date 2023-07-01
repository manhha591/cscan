package com.example.cscan.models;

public class ChangePasswordRequest {
    private String Username ;
    private String CurrentPassword ;
    private String NewPassword ;

    public ChangePasswordRequest(String username, String currentPassword, String newPassword) {
        Username = username;
        CurrentPassword = currentPassword;
        NewPassword = newPassword;
    }
}
