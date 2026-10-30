package com.example.myapplication;

import java.io.Serializable;

public class ScholarshipVerifyItem implements Serializable {
    private String id;
    private String title;
    private String institution;
    private String verificationStatus; // "Not verified", "Pending verification", "Verified"

    public ScholarshipVerifyItem(String id, String title, String institution, String verificationStatus) {
        this.id = id;
        this.title = title;
        this.institution = institution;
        this.verificationStatus = verificationStatus;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getInstitution() { return institution; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}
