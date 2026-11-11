package com.example.myapplication;

import java.io.Serializable;

public class BookmarkedPostItem implements Serializable {
    private String id;
    private String title;
    private String author;
    private String hashtag;

    public BookmarkedPostItem(String id, String title, String author, String hashtag) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.hashtag = hashtag;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getHashtag() { return hashtag; }
}
