package com.example.demo.service;

import com.example.demo.entity.Quiz;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuizResult;
import com.example.demo.entity.Users;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizResultRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.dto.request.SubmitQuizRequest;
import com.example.demo.dto.QuizResultDetailDTO;
import com.example.demo.dto.QuestionResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Quiz getDailyQuiz() {
        return quizRepository.findByDate(LocalDate.now()).orElse(null);
    }

    public QuizResultDetailDTO submitQuiz(SubmitQuizRequest request) throws Exception {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> questions = questionRepository.findByQuiz(quiz);
        int score = 0;
        List<QuestionResultDTO> details = new ArrayList<>();

        for (Question question : questions) {
            String userAnswer = request.getAnswers().get(question.getId());
            boolean isCorrect = userAnswer != null && userAnswer.equals(question.getCorrectAnswer());
            if (isCorrect) {
                score++;
            }
            QuestionResultDTO qr = new QuestionResultDTO(
                question.getId(),
                question.getContent(),
                userAnswer,
                question.getCorrectAnswer(),
                isCorrect,
                question.getExplanation(),
                question.getExplanationLink()
            );
            details.add(qr);
        }

        // Lưu kết quả vào database (nếu cần)
        String answersJson = objectMapper.writeValueAsString(request.getAnswers());
        QuizResult result = new QuizResult(user, quiz, score, questions.size(), answersJson);
        quizResultRepository.save(result);

        // Cập nhật điểm user
        user.setPoints(user.getPoints() + score * 10);
        usersRepository.save(user);

        // Tạo DTO trả về
        QuizResultDetailDTO dto = new QuizResultDetailDTO();
        dto.setScore(score);
        dto.setTotalQuestions(questions.size());
        dto.setPercentage(questions.size() > 0 ? (score * 100 / questions.size()) : 0);
        dto.setPointsEarned(score * 10);
        dto.setDetails(details);
        return dto;
    }

    public List<QuizResult> getUserQuizHistory(Long userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user != null) {
            return quizResultRepository.findByUserOrderByCompletedAtDesc(user);
        }
        return List.of();
    }

    public double getQuizCompletionRate(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz != null) {
            long totalUsers = usersRepository.count();
            long completedCount = quizResultRepository.countByQuiz(quiz);
            return totalUsers > 0 ? (double) completedCount / totalUsers * 100 : 0;
        }
        return 0;
    }
}