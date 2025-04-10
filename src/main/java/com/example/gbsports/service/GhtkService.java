package com.example.gbsports.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GhtkService {

    @Value("${ghtk.token}")
    private String ghtkToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "https://services.giaohangtietkiem.vn";

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghtkToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // 1. Tạo đơn hàng
    public ResponseEntity<String> createOrder(Map<String, Object> orderData) {
        String url = BASE_URL + "/services/shipment/order";
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderData, buildHeaders());
        return restTemplate.postForEntity(url, request, String.class);
    }

    // 2. Hủy đơn hàng
    public ResponseEntity<String> cancelOrder(String orderCode) {
        String url = BASE_URL + "/services/shipment/cancel/" + orderCode;
        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    // 3. Tra cứu đơn hàng
    public ResponseEntity<String> getOrderInfo(String orderCode) {
        String url = BASE_URL + "/services/shipment/v2/" + orderCode;
        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    // 4. Tính phí vận chuyển
    public ResponseEntity<String> calculateFee(String pickProvince, String pickDistrict, String province, String district, int weight, int value) {
        String url = BASE_URL + "/services/shipment/fee?" +
                "pick_province=" + pickProvince +
                "&pick_district=" + pickDistrict +
                "&province=" + province +
                "&district=" + district +
                "&weight=" + weight +
                "&value=" + value;

        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }
}
