package com.example.gbsports.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GhtkOrderRequest {
    private String id; // mã đơn hàng nội bộ
    private String pick_name;
    private String pick_address;
    private String pick_province;
    private String pick_district;
    private String pick_tel;
    private String tel;
    private String name;
    private String address;
    private String province;
    private String district;
    private int weight;
    private int value;
    private int transport; // 1: nhanh, 2: tiết kiệm
    private boolean pick_money;
    private List<Map<String, Object>> products;
}

