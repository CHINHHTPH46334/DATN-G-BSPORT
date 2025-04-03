package com.example.gbsports.repository;

import com.example.gbsports.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    boolean existsByMaVoucher(String maVoucher);

    Optional<Voucher> findByMaVoucher(String maVoucher);

    Page<Voucher> findByTrangThai(String trangThai, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.maVoucher LIKE %:keyword% OR v.tenVoucher LIKE %:keyword%")
    Page<Voucher> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.ngayBatDau >= :startDate AND v.ngayHetHan <= :endDate")
    Page<Voucher> searchByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    Page<Voucher> findByNgayBatDauGreaterThanEqual(LocalDateTime startDate, Pageable pageable);

    Page<Voucher> findByNgayHetHanLessThanEqual(LocalDateTime endDate, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.giaTriToiDa BETWEEN :minPrice AND :maxPrice")
    Page<Voucher> searchByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT COALESCE(MIN(v.giaTriToiDa), 0) FROM Voucher v")
    BigDecimal findMinPrice();

    @Query("SELECT COALESCE(MAX(v.giaTriToiDa), 0) FROM Voucher v")
    BigDecimal findMaxPrice();
}