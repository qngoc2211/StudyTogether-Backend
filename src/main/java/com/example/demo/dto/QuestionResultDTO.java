package com.example.demo.dto;

public class QuestionResultDTO {
    private Long questionId;
    private String questionContent;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;
    private String explanation;
    private String explanationLink;

    public QuestionResultDTO() {}

    public QuestionResultDTO(Long questionId, String questionContent, String userAnswer, String correctAnswer, boolean isCorrect, String explanation, String explanationLink) {
        this.questionId = questionId;
        this.questionContent = questionContent;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
        this.explanationLink = explanationLink;
    }

    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionContent() { return questionContent; }
    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getExplanationLink() { return explanationLink; }
    public void setExplanationLink(String explanationLink) { this.explanationLink = explanationLink; }
}