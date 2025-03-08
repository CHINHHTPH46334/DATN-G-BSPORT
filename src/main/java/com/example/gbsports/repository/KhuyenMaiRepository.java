package com.example.gbsports.repository;


import com.example.gbsports.entity.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai,Integer> {
    @Query("SELECT k FROM KhuyenMai k WHERE " +
            "k.tenKhuyenMai LIKE %:keyword% OR k.maKhuyenMai LIKE %:keyword%")
    Page<KhuyenMai> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    Page<KhuyenMai> findByTrangThai(String trangThai, Pageable pageable);
    boolean existsByMaKhuyenMai(String maKhuyenMai);

}