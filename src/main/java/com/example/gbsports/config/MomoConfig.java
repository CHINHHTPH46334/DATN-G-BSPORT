package com.example.gbsports.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "momo")
public class MomoConfig {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String returnUrl;
    private String notifyUrl;
    private String endpoint;
}