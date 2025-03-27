package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChiTietKhuyenMaiRepo extends JpaRepository<ChiTietKhuyenMai,Integer> {
    @Query("SELECT c FROM ChiTietKhuyenMai c JOIN FETCH c.chiTietSanPham WHERE c.khuyenMai.id = :khuyenMaiId")
    List<ChiTietKhuyenMai> findByKhuyenMaiId(Integer khuyenMaiId);
    void deleteByKhuyenMaiId(Integer khuyenMaiId);
}
