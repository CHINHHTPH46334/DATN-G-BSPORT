package com.example.gbsports.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import lombok.Data;
import java.util.List;
import java.util.Arrays;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "chatbot")
@Data
public class ChatBotConfig {
    // API configuration
    private String geminiApiUrl;
    private String geminiApiKey;
    private int apiTimeoutMs = 10000;
    private int maxRetries = 3;
    private int retryDelayMs = 1000;

    // Cache configuration
    private int maxMessagesPerSession = 20;
    private int maxContextLength = 2000;
    private int sessionTimeoutMinutes = 30;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(apiTimeoutMs);
        factory.setReadTimeout(apiTimeoutMs);
        return new RestTemplate(factory);
    }
}