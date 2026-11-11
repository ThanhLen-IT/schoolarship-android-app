package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;

public class Scholarship implements Serializable {
    private String id;
    private String title;
    private String organization;
    private String country;
    private String degreeLevel; // e.g., "Thạc sĩ", "Đại học", "Tiến sĩ"
    private String fieldOfStudy; // e.g., "Công nghệ thông tin", "Kinh doanh"
    private String fundingType; // e.g., "Toàn phần", "Bán phần"
    private String deadlineStr; // e.g., "17/10/2026"
    private Date deadlineDate;
    private Date updatedAt;
    private double fundingScore; // for sorting by funding

    public Scholarship(String id, String title, String organization, String country, 
                       String degreeLevel, String fieldOfStudy, String fundingType, 
                       String deadlineStr, Date deadlineDate, Date updatedAt, double fundingScore) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.country = country;
        this.degreeLevel = degreeLevel;
        this.fieldOfStudy = fieldOfStudy;
        this.fundingType = fundingType;
        this.deadlineStr = deadlineStr;
        this.deadlineDate = deadlineDate;
        this.updatedAt = updatedAt;
        this.fundingScore = fundingScore;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getCountry() { return country; }
    public String getDegreeLevel() { return degreeLevel; }
    public String getFieldOfStudy() { return fieldOfStudy; }
    public String getFundingType() { return fundingType; }
    public String getDeadlineStr() { return deadlineStr; }
    public Date getDeadlineDate() { return deadlineDate; }
    public Date getUpdatedAt() { return updatedAt; }
    public double getFundingScore() { return fundingScore; }
}
