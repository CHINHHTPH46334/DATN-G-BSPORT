package com.example.gbsports.service;

import com.example.gbsports.cache.ChatbotCache;
import com.example.gbsports.response.SanPhamView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatBotService {

    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    private ChatbotCache chatbotCache;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Xử lý tin nhắn từ người dùng
     */
    public Map<String, Object> processMessage(String userMessage, String sessionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Lưu tin nhắn người dùng vào cache
            chatbotCache.saveMessage(userMessage, sessionId, true);

            // Kiểm tra xem có phải là câu chào hỏi không
            String greeting = checkGreeting(userMessage.toLowerCase());
            if (greeting != null) {
                chatbotCache.saveMessage(greeting, sessionId, false);
                chatbotCache.updateContext(userMessage, greeting, sessionId);
                response.put("message", greeting);
                return response;
            }
            
            // Phân tích ý định và trích xuất thông tin sản phẩm
            Map<String, Object> productInfo = extractProductInfo(userMessage);
            
            // Tìm kiếm sản phẩm dựa trên thông tin trích xuất được
            List<SanPhamView> products = searchProducts(productInfo);
            
            // Tạo phản hồi cho người dùng
            String botResponse;
            if (products.isEmpty()) {
                botResponse = "Xin lỗi, tôi không tìm thấy sản phẩm nào phù hợp với yêu cầu của bạn. Bạn có thể mô tả chi tiết hơn về sản phẩm bạn đang tìm kiếm không?";
            } else {
                botResponse = generateProductListResponse(products, productInfo);
            }
            
            // Lưu phản hồi chatbot vào cache
            chatbotCache.saveMessage(botResponse, sessionId, false);
            
            // Cập nhật context cuộc trò chuyện
            chatbotCache.updateContext(userMessage, botResponse, sessionId);
            
            response.put("message", botResponse);
            response.put("products", products);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại sau.");
        }
        
        return response;
    }

    /**
     * Kiểm tra và trả về câu chào phù hợp
     */
    private String checkGreeting(String message) {
        // Danh sách các từ khóa chào hỏi
        List<String> greetingKeywords = Arrays.asList(
            "xin chào", "chào", "hello", "hi", "hey", "alo", "chào bạn",
            "good morning", "good afternoon", "good evening"
        );

        // Kiểm tra nếu tin nhắn chứa từ khóa chào hỏi
        boolean isGreeting = greetingKeywords.stream()
                .anyMatch(keyword -> message.contains(keyword));

        if (isGreeting) {
            List<String> greetings = Arrays.asList(
                "Xin chào! Tôi là trợ lý ảo của G-BSport. Tôi có thể giúp bạn tìm kiếm sản phẩm hoặc trả lời các câu hỏi về cửa hàng.",
                "Chào bạn! Rất vui được hỗ trợ bạn hôm nay. Bạn cần tìm sản phẩm gì?",
                "Xin chào! Tôi có thể giúp gì cho bạn?",
                "Chào mừng bạn đến với G-BSport! Tôi có thể giúp bạn tìm kiếm các sản phẩm thể thao bạn cần."
            );
            
            // Chọn ngẫu nhiên một câu chào
            Random random = new Random();
            return greetings.get(random.nextInt(greetings.size()));
        }

        return null;
    }

    /**
     * Trích xuất thông tin sản phẩm từ tin nhắn người dùng sử dụng Gemini API
     */
    private Map<String, Object> extractProductInfo(String userMessage) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Tạo request cho Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiApiKey);
            
            // Tạo prompt để trích xuất thông tin
            String prompt = "Từ tin nhắn sau, hãy trích xuất các thông tin liên quan đến sản phẩm như: " +
                    "tên sản phẩm, danh mục, thương hiệu, màu sắc, kích thước, giá (khoảng giá). " +
                    "Trả về dưới dạng JSON với các field là: ten_san_pham, danh_muc, thuong_hieu, mauSac, kichThuoc, giaMin, giaMax. " +
                    "Tin nhắn: \"" + userMessage + "\"";
            
            // Tạo body request
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            contents.put("role", "user");
            contents.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
            requestBody.put("contents", Collections.singletonList(contents));
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl, request, String.class);
            
            // Xử lý response để lấy JSON thông tin sản phẩm
            JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
            String generatedText = jsonResponse
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")  // Đúng cấu trúc json từ Gemini API
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
            
            // Trích xuất phần JSON từ response
            int startIndex = generatedText.indexOf("{");
            int endIndex = generatedText.lastIndexOf("}") + 1;
            
            if (startIndex >= 0 && endIndex > startIndex) {
                String jsonString = generatedText.substring(startIndex, endIndex);
                JsonObject productInfo = new JsonParser().parse(jsonString).getAsJsonObject();
                
                // Thêm các thông tin vào result
                if (productInfo.has("ten_san_pham") && !productInfo.get("ten_san_pham").isJsonNull())
                    result.put("tenSanPham", productInfo.get("ten_san_pham").getAsString());
                    
                if (productInfo.has("danh_muc") && !productInfo.get("danh_muc").isJsonNull())
                    result.put("listDanhMuc", Collections.singletonList(productInfo.get("danh_muc").getAsString()));
                    
                if (productInfo.has("thuong_hieu") && !productInfo.get("thuong_hieu").isJsonNull())
                    result.put("listThuongHieu", Collections.singletonList(productInfo.get("thuong_hieu").getAsString()));
                    
                if (productInfo.has("mauSac") && !productInfo.get("mauSac").isJsonNull())
                    result.put("listMauSac", Collections.singletonList(productInfo.get("mauSac").getAsString()));
                    
                if (productInfo.has("kichThuoc") && !productInfo.get("kichThuoc").isJsonNull())
                    result.put("listKichThuoc", Collections.singletonList(productInfo.get("kichThuoc").getAsString()));
                    
                if (productInfo.has("giaMin") && !productInfo.get("giaMin").isJsonNull())
                    result.put("giaBanMin", productInfo.get("giaMin").getAsFloat());
                    
                if (productInfo.has("giaMax") && !productInfo.get("giaMax").isJsonNull())
                    result.put("giaBanMax", productInfo.get("giaMax").getAsFloat());
                
                // Thêm từ khóa tìm kiếm
                result.put("keyword", extractKeywords(userMessage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * Trích xuất từ khóa tìm kiếm từ tin nhắn người dùng
     */
    private String extractKeywords(String userMessage) {
        // Logic đơn giản: loại bỏ các từ thông dụng và giữ lại các từ có ý nghĩa
        List<String> stopWords = Arrays.asList("tôi", "muốn", "tìm", "kiếm", "mua", "sản", "phẩm", "cho", "với", "và", "hay", "hoặc", "là", "có");
        
        String[] words = userMessage.toLowerCase().split("\\s+");
        return Arrays.stream(words)
                .filter(word -> word.length() > 2 && !stopWords.contains(word))
                .collect(Collectors.joining(" "));
    }

    /**
     * Tìm kiếm sản phẩm sử dụng ChiTietSanPhamService
     */
    private List<SanPhamView> searchProducts(Map<String, Object> criteria) {
        try {
            ResponseEntity<List<SanPhamView>> response = chiTietSanPhamService.timKiemVaLoc(
                    (String) criteria.getOrDefault("keyword", null),
                    (String) criteria.getOrDefault("tenSanPham", null),
                    (Float) criteria.getOrDefault("giaBanMin", null),
                    (Float) criteria.getOrDefault("giaBanMax", null),
                    (Integer) criteria.getOrDefault("soLuongMin", null),
                    (Integer) criteria.getOrDefault("soLuongMax", null),
                    (String) criteria.getOrDefault("trangThai", "Hoạt động"),
                    (List<String>) criteria.getOrDefault("listMauSac", null),
                    (List<String>) criteria.getOrDefault("listDanhMuc", null),
                    (List<String>) criteria.getOrDefault("listThuongHieu", null),
                    (List<String>) criteria.getOrDefault("listChatLieu", null),
                    (List<String>) criteria.getOrDefault("listKichThuoc", null)
            );
            
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tạo phản hồi danh sách sản phẩm
     */
    private String generateProductListResponse(List<SanPhamView> products, Map<String, Object> criteria) {
        StringBuilder response = new StringBuilder();
        
        response.append("Tôi đã tìm thấy ").append(products.size()).append(" sản phẩm phù hợp với yêu cầu của bạn:\n\n");
        
        // Giới hạn số lượng sản phẩm hiển thị
        int displayLimit = Math.min(products.size(), 5);
        
        for (int i = 0; i < displayLimit; i++) {
            SanPhamView product = products.get(i);
            response.append(i + 1).append(". ").append(product.getTen_san_pham());
            
            // Thêm thông tin thương hiệu nếu có
            if (product.getTen_thuong_hieu() != null && !product.getTen_thuong_hieu().isEmpty()) {
                response.append(" - ").append(product.getTen_thuong_hieu());
            }
            
            // Thêm thông tin giá nếu có
            if (product.getGia_tot_nhat() != null) {
                response.append(" - Giá: ").append(formatCurrency(product.getGia_tot_nhat().doubleValue()));
            }
            
            response.append("\n");
        }
        
        // Nếu có nhiều hơn limit sản phẩm, thêm thông báo
        if (products.size() > displayLimit) {
            response.append("\nVà ").append(products.size() - displayLimit).append(" sản phẩm khác. ");
        }
        
        response.append("\nBạn có thể hỏi thêm thông tin chi tiết về sản phẩm nào hoặc tiếp tục tìm kiếm với các tiêu chí khác.");
        
        return response.toString();
    }

    /**
     * Format giá tiền
     */
    private String formatCurrency(Double amount) {
        if (amount == null) return "";
        return String.format("%,.0f₫", amount);
    }
}
