package com.example.gbsports.service;

import com.example.gbsports.entity.SanPham;
import com.example.gbsports.repository.SanPhamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SanPhamService {
    
    private final SanPhamRepo sanPhamRepo;

    public Page<SanPham> searchProducts(String keyword, Integer categoryId, Integer brandId, Integer materialId, Pageable pageable) {
        // If no filters are provided, return all products
        if ((keyword == null || keyword.trim().isEmpty()) 
            && categoryId == null 
            && brandId == null 
            && materialId == null) {
            return sanPhamRepo.findAll(pageable);
        }
        
        // Use the enhanced search method from repository
        return sanPhamRepo.searchSanPhamAdvanced(
            keyword, 
            categoryId, 
            brandId, 
            materialId, 
            pageable
        );
    }
}
