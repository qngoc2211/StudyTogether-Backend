package com.example.demo.dto;

import java.util.List;

public class QuizResultDetailDTO {
    private int score;
    private int totalQuestions;
    private int percentage;
    private int pointsEarned;
    private List<QuestionResultDTO> details;

    // Constructor mặc định
    public QuizResultDetailDTO() {}

    // Constructor đầy đủ
    public QuizResultDetailDTO(int score, int totalQuestions, int percentage, int pointsEarned, List<QuestionResultDTO> details) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.pointsEarned = pointsEarned;
        this.details = details;
    }

    // Inner class cho chi tiết từng câu hỏi
    public static class QuestionResultDTO {
        private Long questionId;
        private String questionContent;
        private String userAnswer;
        private String correctAnswer;
        private boolean isCorrect;
        private String explanation;
        private String explanationLink; // Link tham khảo (nếu có)

        // Constructor mặc định
        public QuestionResultDTO() {}

        // Constructor đầy đủ
        public QuestionResultDTO(Long questionId, String questionContent, String userAnswer,
                                  String correctAnswer, boolean isCorrect,
                                  String explanation, String explanationLink) {
            this.questionId = questionId;
            this.questionContent = questionContent;
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.isCorrect = isCorrect;
            this.explanation = explanation;
            this.explanationLink = explanationLink;
        }

        // Getters and Setters
        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getQuestionContent() {
            return questionContent;
        }

        public void setQuestionContent(String questionContent) {
            this.questionContent = questionContent;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getExplanationLink() {
            return explanationLink;
        }

        public void setExplanationLink(String explanationLink) {
            this.explanationLink = explanationLink;
        }
    }

    // Getters and Setters cho lớp ngoài
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public List<QuestionResultDTO> getDetails() {
        return details;
    }

    public void setDetails(List<QuestionResultDTO> details) {
        this.details = details;
    }
}