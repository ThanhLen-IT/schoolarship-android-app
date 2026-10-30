package com.example.myapplication;

import java.io.Serializable;

public class HomeSliderItem implements Serializable {
    private String title;
    private String description;
    private String ctaText;
    private int imageResId;

    public HomeSliderItem(String title, String description, String ctaText, int imageResId) {
        this.title = title;
        this.description = description;
        this.ctaText = ctaText;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCtaText() { return ctaText; }
    public int getImageResId() { return imageResId; }
}
