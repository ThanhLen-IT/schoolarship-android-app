package com.example.myapplication;

public class RegisterRequest {
    private String email;
    private String password;
    private String full_name;
    private String username;
    private String university;
    private String major;

    public RegisterRequest(String email, String password, String full_name, String username, String university, String major) {
        this.email = email;
        this.password = password;
        this.full_name = full_name;
        this.username = username;
        this.university = university;
        this.major = major;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return full_name; }
    public String getUsername() { return username; }
    public String getUniversity() { return university; }
    public String getMajor() { return major; }
}
