package com.example.demo.controller;

import com.example.demo.dto.AICoachRequest;
import com.example.demo.dto.AICoachRequest.Mistake;
import com.example.demo.service.AICoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/test")
public class AITestController {

    @Autowired
    private AICoachService aiCoachService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> testAI() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Tạo một request giả với một câu hỏi mẫu
            List<Mistake> mistakes = new ArrayList<>();
            Mistake m = new Mistake();
            m.setQuestion("Câu hỏi test: 1+1 bằng mấy?");
            m.setUserAnswer("3");
            m.setCorrectAnswer("2");
            m.setExplanation("Phép tính đơn giản");
            m.setExplanationLink("https://example.com");
            mistakes.add(m);

            AICoachRequest request = new AICoachRequest();
            request.setMistakes(mistakes);

            String advice = aiCoachService.generateAdvice(request);
            result.put("success", true);
            result.put("advice", advice);
            result.put("message", "Gọi AI thành công, kiểm tra nội dung advice để xem có phải từ AI không");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("stacktrace", e.getStackTrace());
        }
        return ResponseEntity.ok(result);
    }
}