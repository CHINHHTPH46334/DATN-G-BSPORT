package com.example.gbsports.repository;

import com.example.gbsports.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    List<KhuyenMai> findByTrangThai(String trangThai);

    @Query("SELECT km FROM KhuyenMai km WHERE km.maKhuyenMai LIKE %:keyword% OR km.tenKhuyenMai LIKE %:keyword%")
    List<KhuyenMai> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT km FROM KhuyenMai km WHERE (:start IS NULL OR km.ngayBatDau >= :start) AND (:end IS NULL OR km.ngayHetHan <= :end)")
    List<KhuyenMai> searchByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT km FROM KhuyenMai km WHERE km.giaTriToiDa BETWEEN :minPrice AND :maxPrice")
    List<KhuyenMai> searchByGiaTriToiDaRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    @Query("SELECT MIN(km.giaTriToiDa) FROM KhuyenMai km")
    Integer findMinGiaTriToiDa();

    @Query("SELECT MAX(km.giaTriToiDa) FROM KhuyenMai km")
    Integer findMaxGiaTriToiDa();
}