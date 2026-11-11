package com.example.myapplication;

public class UserProfile {
    private String id;
    private String email;
    private String full_name;
    private String username;
    private String university;
    private String major;
    private String bio;
    private java.util.List<String> skills;
    private boolean is_mentor;
    private String avatar_url;
    private String role;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return full_name;
    }

    public String getUsername() {
        return username;
    }

    public String getUniversity() {
        return university;
    }

    public String getMajor() {
        return major;
    }

    public String getBio() {
        return bio;
    }

    public java.util.List<String> getSkills() {
        return skills;
    }

    public boolean isVerifiedMentor() {
        return is_mentor && "mentor".equals(role);
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public String getRole() {
        return role;
    }
}
