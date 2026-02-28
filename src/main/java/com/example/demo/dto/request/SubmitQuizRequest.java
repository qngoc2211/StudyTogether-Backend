package com.example.demo.dto.request;

import java.util.List;
import java.util.Map;

public class SubmitQuizRequest {
    private Long quizId;
    private Long userId;
    private Map<Long, String> answers; // questionId -> answer
    private int timeSpent; // thời gian làm bài (giây)

    // Getters và Setters
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Map<Long, String> getAnswers() { return answers; }
    public void setAnswers(Map<Long, String> answers) { this.answers = answers; }

    public int getTimeSpent() { return timeSpent; }
    public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }
}