package com.example.gbsports.controller;

import com.example.gbsports.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    /**
     * API xử lý tin nhắn từ khách hàng
     */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> processMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        // Lấy sessionId từ request nếu có, nếu không thì tạo mới
        String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xin vui lòng nhập tin nhắn");
            response.put("sessionId", sessionId);
            return ResponseEntity.badRequest().body(response);
        }
        
        // Xử lý tin nhắn qua ChatBotService
        Map<String, Object> result = chatBotService.processMessage(userMessage, sessionId);
        
        // Thêm sessionId vào response để client lưu lại và gửi lên trong các request tiếp theo
        result.put("sessionId", sessionId);
        
        return ResponseEntity.ok(result);
    }

    /**
     * API lấy lịch sử trò chuyện
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getMessageHistory(@RequestParam String sessionId) {
        // Triển khai sau khi có phương thức lấy lịch sử tin nhắn
        // Hiện tại chỉ trả về thông báo
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tính năng xem lịch sử trò chuyện sẽ được triển khai sau");
        response.put("sessionId", sessionId);
        
        return ResponseEntity.ok(response);
    }
}
