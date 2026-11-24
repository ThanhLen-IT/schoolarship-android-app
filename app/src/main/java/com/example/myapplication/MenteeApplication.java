package com.example.myapplication;

import java.io.Serializable;

public class MenteeApplication implements Serializable {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    private String id;
    private String fullName;
    private String initials;
    private String university;
    private String scholarshipName;
    private String description;
    private Status status;

    public MenteeApplication(String id, String fullName, String initials, String university, String scholarshipName, String description, Status status) {
        this.id = id;
        this.fullName = fullName;
        this.initials = initials;
        this.university = university;
        this.scholarshipName = scholarshipName;
        this.description = description;
        this.status = status;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getInitials() { return initials; }
    public String getUniversity() { return university; }
    public String getScholarshipName() { return scholarshipName; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
