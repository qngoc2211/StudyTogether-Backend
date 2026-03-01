package com.example.demo.dto.request;

public record UpdatePostRequest(
        String title,
        String content,
        String category,
        Boolean locked
) {}