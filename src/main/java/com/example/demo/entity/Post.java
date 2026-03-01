package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String author;
    private String category;
    private Boolean locked;
    private Integer viewCount;
    private LocalDateTime createdAt;

    public Post() {
    }

    public Post(String title, String content, String author, String category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.locked = false;
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public Boolean getLocked() { return locked; }
    public Integer getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCategory(String category) { this.category = category; }
    public void setLocked(Boolean locked) { this.locked = locked; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
}