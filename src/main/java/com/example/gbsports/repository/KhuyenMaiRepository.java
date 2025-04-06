package com.example.gbsports.repository;

import com.example.gbsports.entity.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai,Integer> {

//    boolean existsByMaKhuyenMai(String maKhuyenMai);
//
//    Page<KhuyenMai> findAll(Pageable pageable);
//
//    @Query("SELECT km FROM KhuyenMai km WHERE km.maKhuyenMai LIKE %:keyword% OR km.tenKhuyenMai LIKE %:keyword%")
//    Page<KhuyenMai> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
//
//    Page<KhuyenMai> findByTrangThai(String trangThai, Pageable pageable);
//
//    @Query("SELECT km FROM KhuyenMai km WHERE (:start IS NULL OR km.ngayBatDau >= :start) AND (:end IS NULL OR km.ngayHetHan <= :end)")
//    Page<KhuyenMai> searchByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
//
//    @Query("SELECT km FROM KhuyenMai km WHERE km.giaTriToiDa BETWEEN :minPrice AND :maxPrice")
//    Page<KhuyenMai> searchByGiaTriToiDaRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);
//
//    @Query("SELECT MIN(km.giaTriToiDa) FROM KhuyenMai km")
//    Integer findMinGiaTriToiDa();
//
//    @Query("SELECT MAX(km.giaTriToiDa) FROM KhuyenMai km")
//    Integer findMaxGiaTriToiDa();
//    Optional<KhuyenMai> findByMaKhuyenMai(String maKhuyenMai);
boolean existsByMaKhuyenMai(String maKhuyenMai);

    Optional<KhuyenMai> findByMaKhuyenMai(String maKhuyenMai);

    Page<KhuyenMai> findByTrangThai(String trangThai, Pageable pageable);

    @Query("SELECT km FROM KhuyenMai km WHERE km.maKhuyenMai LIKE %:keyword% OR km.tenKhuyenMai LIKE %:keyword%")
    Page<KhuyenMai> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT km FROM KhuyenMai km " +
            "WHERE (:start IS NULL OR km.ngayBatDau >= :start) " +
            "AND (:end IS NULL OR km.ngayHetHan <= :end)")
    Page<KhuyenMai> searchByDateRange(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, Pageable pageable);

    @Query("SELECT km FROM KhuyenMai km WHERE km.giaTriGiam BETWEEN :minPrice AND :maxPrice")
    Page<KhuyenMai> searchByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT COALESCE(MIN(km.giaTriGiam), 0) FROM KhuyenMai km")
    BigDecimal findMinPrice();

    @Query("SELECT COALESCE(MAX(km.giaTriGiam), 0) FROM KhuyenMai km")
    BigDecimal findMaxPrice();
}