package com.example.gbsports.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**")  // URL để truy cập ảnh
                .addResourceLocations("file:C:\\Users\\Hawin\\Desktop\\Avatar\\") // Thư mục chứa ảnh trên máy
                .setCachePeriod(0) // Tắt cache
                .resourceChain(true)  // Thêm dòng này
                .addResolver(new PathResourceResolver());
    }
}
