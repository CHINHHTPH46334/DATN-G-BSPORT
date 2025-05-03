package com.example.gbsports.cache;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatbotCache {

    // Lưu trữ tin nhắn theo sessionId
    private final Map<String, List<ChatMessage>> messageCache = new ConcurrentHashMap<>();

    // Lưu trữ context theo sessionId
    private final Map<String, ChatContext> contextCache = new ConcurrentHashMap<>();

    // Thời gian hết hạn cache (mặc định 30 phút)
    private static final long CACHE_EXPIRY_MINUTES = 30;

    /**
     * Lưu tin nhắn vào cache
     */
    public void saveMessage(String message, String sessionId, boolean isUserMessage) {
        List<ChatMessage> messages = messageCache.computeIfAbsent(sessionId, k -> new ArrayList<>());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setUserMessage(isUserMessage);
        chatMessage.setTimestamp(LocalDateTime.now());

        messages.add(chatMessage);

        // Giới hạn số lượng tin nhắn lưu trữ (giữ 20 tin nhắn gần nhất)
        if (messages.size() > 20) {
            messages.remove(0);
        }
    }

    /**
     * Lấy danh sách tin nhắn theo sessionId
     */
    public List<ChatMessage> getMessagesBySessionId(String sessionId) {
        return messageCache.getOrDefault(sessionId, new ArrayList<>());
    }

    /**
     * Cập nhật context của cuộc trò chuyện
     */
    public void updateContext(String userMessage, String botResponse, String sessionId) {
        ChatContext context = contextCache.computeIfAbsent(sessionId, k -> new ChatContext());

        StringBuilder contextBuilder = new StringBuilder();
        if (context.getContext() != null && !context.getContext().isEmpty()) {
            contextBuilder.append(context.getContext()).append("\n");
        }

        // Thêm tin nhắn mới vào context
        contextBuilder.append("Người dùng: ").append(userMessage).append("\n");
        contextBuilder.append("Bot: ").append(botResponse).append("\n");

        // Giới hạn kích thước context
        String newContext = contextBuilder.toString();
        if (newContext.length() > 2000) {
            newContext = newContext.substring(newContext.length() - 2000);
        }

        context.setContext(newContext);
        context.setLastUpdated(LocalDateTime.now());
    }

    /**
     * Lấy context theo sessionId
     */
    public ChatContext getContextBySessionId(String sessionId) {
        return contextCache.getOrDefault(sessionId, new ChatContext());
    }

    /**
     * Xóa các cache hết hạn
     * Phương thức này có thể được gọi định kỳ bởi một scheduled task
     */
    public void cleanExpiredCache() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(CACHE_EXPIRY_MINUTES);

        // Xóa tin nhắn hết hạn
        messageCache.entrySet().removeIf(entry -> {
            if (entry.getValue().isEmpty()) return true;
            LocalDateTime lastMessageTime = entry.getValue().get(entry.getValue().size() - 1).getTimestamp();
            return lastMessageTime.isBefore(expiryTime);
        });

        // Xóa context hết hạn
        contextCache.entrySet().removeIf(entry ->
                entry.getValue().getLastUpdated().isBefore(expiryTime));
    }

    /**
     * Lớp nội bộ để lưu trữ tin nhắn
     */
    @Data
    public static class ChatMessage {
        private String message;
        private boolean isUserMessage;
        private LocalDateTime timestamp;
    }

    /**
     * Lớp nội bộ để lưu trữ context
     */
    @Data
    public static class ChatContext {
        private String context = "";
        private LocalDateTime lastUpdated = LocalDateTime.now();
    }
}