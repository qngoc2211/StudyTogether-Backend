package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;

public class UpdatePostRequest {
    @Size(min = 5, max = 200, message = "Tiêu đề phải từ 5-200 ký tự")
    private String title;
    
    @Size(min = 10, message = "Nội dung phải có ít nhất 10 ký tự")
    private String content;
    
    private String category;
    
    private Boolean locked;

    // Getters và Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Boolean getLocked() { return locked; }
    public void setLocked(Boolean locked) { this.locked = locked; }
}