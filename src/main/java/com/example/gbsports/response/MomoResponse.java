package com.example.gbsports.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoResponse {
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
    private String errorCode;
    private String message;
}
