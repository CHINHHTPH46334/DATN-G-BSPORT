package com.example.gbsports.request;

import lombok.Data;

@Data
public class MoMoPaymentRequest {
    private String orderId;
    private String amount;
    private String orderInfo;
}
