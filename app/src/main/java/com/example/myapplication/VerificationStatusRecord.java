package com.example.myapplication;

import java.io.Serializable;

public class VerificationStatusRecord implements Serializable {
    private String id;
    private String title;
    private String institution;
    private String status; // "Approved", "Pending Review", "Failed / Rejected"
    private String dateOrNote; // e.g. "Verified on Jan 12, 2025" or "Submitted on Feb 08, 2025" or "Needs updated proof"

    public VerificationStatusRecord(String id, String title, String institution, String status, String dateOrNote) {
        this.id = id;
        this.title = title;
        this.institution = institution;
        this.status = status;
        this.dateOrNote = dateOrNote;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getInstitution() { return institution; }
    public String getStatus() { return status; }
    public String getDateOrNote() { return dateOrNote; }
}
