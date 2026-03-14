package com.example.demo.service;

import com.example.demo.dto.AICoachRequest;
import com.example.demo.dto.AICoachRequest.Mistake;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AICoachService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.model:deepseek-chat}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateAdvice(AICoachRequest request) {
        List<Mistake> mistakes = request.getMistakes();
        if (mistakes == null || mistakes.isEmpty()) {
            return "🎉 Chúc mừng! Bạn đã trả lời đúng tất cả các câu hỏi. Hãy tiếp tục phát huy!";
        }

        String prompt = buildPrompt(mistakes);

        try {
            return callAI(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return buildFallbackAdvice(mistakes);
        }
    }

    private String buildPrompt(List<Mistake> mistakes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bạn là một gia sư AI thông minh, chuyên phân tích lỗi sai và đưa ra lời khuyên học tập. ");
        sb.append("Hãy xem xét các câu hỏi mà người học đã trả lời sai dưới đây, phân tích xem họ yếu ở mảng kiến thức nào, ");
        sb.append("và đề xuất phương pháp cải thiện cụ thể (bài đọc, video, bài tập, v.v.). Trả lời bằng tiếng Việt, thân thiện và chi tiết.\n\n");
        sb.append("📋 **DANH SÁCH CÂU SAI**\n");

        for (int i = 0; i < mistakes.size(); i++) {
            Mistake m = mistakes.get(i);
            sb.append("**Câu ").append(i + 1).append(":** ").append(m.getQuestion()).append("\n");
            sb.append("   - ❌ Đáp án của bạn: ").append(m.getUserAnswer()).append("\n");
            sb.append("   - ✅ Đáp án đúng: ").append(m.getCorrectAnswer()).append("\n");
            if (m.getExplanation() != null && !m.getExplanation().isEmpty()) {
                sb.append("   - 📘 Giải thích: ").append(m.getExplanation()).append("\n");
            }
            if (m.getExplanationLink() != null && !m.getExplanationLink().isEmpty()) {
                sb.append("   - 🔗 Bài viết tham khảo: ").append(m.getExplanationLink()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("Dựa trên thông tin trên, hãy giúp tôi:\n");
        sb.append("1. **Phân tích** tôi đang yếu ở những chủ đề / mảng kiến thức nào?\n");
        sb.append("2. **Đề xuất** các nguồn học tập phù hợp (bài viết, sách, khóa học, video) – nếu có link cụ thể từ dữ liệu trên thì khuyến khích sử dụng.\n");
        sb.append("3. **Gợi ý lộ trình ôn tập** trong 1-2 tuần tới, bao gồm cả thời gian biểu gợi ý.\n");
        sb.append("Trả lời bằng tiếng Việt, văn phong gần gũi, có thể dùng emoji để sinh động.");

        return sb.toString();
    }

    private String callAI(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "Bạn là một trợ lý AI chuyên về giáo dục, giúp sinh viên cải thiện kiến thức."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);
        requestBody.put("top_p", 0.9);
        requestBody.put("frequency_penalty", 0.5);
        requestBody.put("presence_penalty", 0.5);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        // DeepSeek trả về theo format OpenAI: choices[0].message.content
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private String buildFallbackAdvice(List<Mistake> mistakes) {
        StringBuilder advice = new StringBuilder("⚠️ **Hiện tại AI chưa thể kết nối, nhưng bạn có thể ôn tập theo gợi ý dưới đây:**\n\n");

        for (Mistake m : mistakes) {
            advice.append("📌 ").append(m.getQuestion()).append("\n");
            advice.append("   ➤ Bạn đã chọn: ").append(m.getUserAnswer()).append("\n");
            advice.append("   ➤ Đáp án đúng: ").append(m.getCorrectAnswer()).append("\n");
            if (m.getExplanation() != null && !m.getExplanation().isEmpty()) {
                advice.append("   📘 ").append(m.getExplanation()).append("\n");
            }
            if (m.getExplanationLink() != null && !m.getExplanationLink().isEmpty()) {
                advice.append("   🔗 Đọc thêm: ").append(m.getExplanationLink()).append("\n");
            }
            advice.append("\n");
        }

        advice.append("💡 Hãy dành thời gian đọc lại các bài viết được đề cập để củng cố kiến thức. Nếu cần hỗ trợ thêm, hãy thử lại sau!");
        return advice.toString();
    }
}