package com.example.gbsports.controller;

import com.example.gbsports.service.GhtkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ghtk")
public class GHTKController {

    @Autowired
    private GhtkService ghtkService;

    // 1. Tạo đơn hàng
    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> orderData) {
        return ghtkService.createOrder(orderData);
    }

    // 2. Hủy đơn hàng
    @PostMapping("/cancel/{orderCode}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderCode) {
        return ghtkService.cancelOrder(orderCode);
    }

    // 3. Tra cứu đơn hàng
    @GetMapping("/info/{orderCode}")
    public ResponseEntity<String> getOrderInfo(@PathVariable String orderCode) {
        return ghtkService.getOrderInfo(orderCode);
    }

    // 4. Tính phí vận chuyển
    @GetMapping("/fee")
    public ResponseEntity<String> calculateFee(
            @RequestParam String pickProvince,
            @RequestParam String pickDistrict,
            @RequestParam String province,
            @RequestParam String district,
            @RequestParam int weight,
            @RequestParam int value
    ) {
        return ghtkService.calculateFee(pickProvince, pickDistrict, province, district, weight, value);
    }
}
