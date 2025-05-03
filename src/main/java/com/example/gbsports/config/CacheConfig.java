package com.example.gbsports.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Cấu hình chung cho tất cả các cache
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(100)
                .maximumSize(500));

        // Danh sách tên cache
        cacheManager.setCacheNames(Arrays.asList("products","ctspBySp"));

        return cacheManager;
    }
//    @Bean
//    public CacheManager detailCacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//
//        // Cache cho chi tiết sản phẩm
//        Cache chiTietSanPhamCache = new CaffeineCache("chiTietSanPhamCache",
//                Caffeine.newBuilder()
//                        .expireAfterWrite(5, TimeUnit.MINUTES)
//                        .maximumSize(500)
//                        .recordStats() // Ghi lại thống kê
//                        .build());
//
//        // Cache khác với cấu hình riêng
//        Cache otherCache = new CaffeineCache("otherCache",
//                Caffeine.newBuilder()
//                        .expireAfterAccess(30, TimeUnit.MINUTES)
//                        .maximumSize(1000)
//                        .build());
//
//        cacheManager.setCaches(Arrays.asList(chiTietSanPhamCache, otherCache));
//        return cacheManager;
//    }
}
