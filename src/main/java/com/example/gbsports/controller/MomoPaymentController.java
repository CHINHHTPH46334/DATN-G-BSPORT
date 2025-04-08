package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.response.MomoResponse;
import com.example.gbsports.service.MomoService;
import com.example.gbsports.util.HMACUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequiredArgsConstructor
@RequestMapping("/api/momo")
public class MomoPaymentController {

    @Value("${momo.partnerCode}")
    private String partnerCode;
    @Value("${momo.accessKey}")
    private String accessKey;
    @Value("${momo.secretKey}")
    private String secretKey;
    @Value("${momo.redirectUrl}")
    private String redirectUrl;
    @Value("${momo.ipnUrl}")
    private String ipnUrl;
    @Value("${momo.requestType}")
    private String requestType;
    @Value("${momo.endpoint}")
    private String endpoint;

    @Autowired
    private MomoService momoService;

    @Autowired
    private HoaDonRepo hoaDonRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/create")
    public String createMomoPayment(Long amount, String orderId, String orderInfo) throws Exception {
        String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        String partnerCode = "MOMO"; // test partnerCode từ MoMo
        String accessKey = "F8BBA842ECF85";
        String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
        String redirectUrl = "http://localhost:5173/admin"; // Quay lại sau khi thanh toán
        String ipnUrl = "http://localhost:8080/api/momo/ipn"; // Webhook nếu cần
        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";
        String extraData = ""; // Có thể bỏ trống

        // Tạo chuỗi raw để ký
        String rawHash = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        // Ký SHA256 bằng secretKey
        String signature = HMACUtil.hmacSha256Hex(secretKey, rawHash);

        // Tạo JSON body gửi lên MoMo
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("partnerCode", partnerCode);
        jsonBody.put("accessKey", accessKey);
        jsonBody.put("requestId", requestId);
        jsonBody.put("amount", amount);
        jsonBody.put("orderId", orderId);
        jsonBody.put("orderInfo", orderInfo);
        jsonBody.put("redirectUrl", redirectUrl);
        jsonBody.put("ipnUrl", ipnUrl);
        jsonBody.put("extraData", extraData);
        jsonBody.put("requestType", requestType);
        jsonBody.put("signature", signature);
        jsonBody.put("lang", "vi");

        // Gửi HTTP POST
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject responseJson = new JSONObject(response.body());

        // Trả về URL để redirect sang trang thanh toán MoMo
        return responseJson.getString("payUrl");
    }

    @PostMapping("/thanhToanMomo")
    public ResponseEntity<?> thanhToanMomo(@RequestParam("idHoaDon") Integer idHoaDon) {
        try {
            MomoResponse response = momoService.handleMomoPayment(idHoaDon);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thanh toán thất bại");
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<String> momoCallback(@RequestBody Map<String, Object> payload) {
        try {
            String resultCode = String.valueOf(payload.get("resultCode"));
            String orderId = (String) payload.get("orderId");

            System.out.println("MoMo callback: " + payload);

            if ("0".equals(resultCode)) { // Thành công
                Integer hoaDonId = Integer.parseInt(orderId.replace("HD", ""));
                HoaDon hoaDon = hoaDonRepo.findById(hoaDonId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

                hoaDon.setTrang_thai("Đã thanh toán");
                hoaDonRepo.save(hoaDon);
                return ResponseEntity.ok("Cập nhật thành công");
            } else {
                return ResponseEntity.ok("Thanh toán thất bại");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi xử lý callback: " + e.getMessage());
        }
    }



}
