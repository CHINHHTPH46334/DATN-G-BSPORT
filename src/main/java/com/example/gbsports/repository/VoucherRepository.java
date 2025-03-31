package com.example.gbsports.repository;

import com.example.gbsports.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface VoucherRepository extends JpaRepository<Voucher,Integer> {

    boolean existsByMaVoucher(String maVoucher);

    // Thêm phương thức findByMaVoucher
    Optional<Voucher> findByMaVoucher(String maVoucher);

    List<Voucher> findAll();

    @Query("SELECT v FROM Voucher v WHERE v.maVoucher LIKE %:keyword% OR v.tenVoucher LIKE %:keyword%")
    List<Voucher> searchByKeyword(@Param("keyword") String keyword);

    List<Voucher> findByTrangThai(String trangThai);

    @Query("SELECT v FROM Voucher v WHERE (:start IS NULL OR v.ngayBatDau >= :start) AND (:end IS NULL OR v.ngayHetHan <= :end)")
    List<Voucher> searchByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT v FROM Voucher v WHERE v.giaTriToiDa BETWEEN :minPrice AND :maxPrice")
    List<Voucher> searchByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    @Query("SELECT MIN(v.giaTriToiDa) FROM Voucher v")
    Integer findMinPrice();

    @Query("SELECT MAX(v.giaTriToiDa) FROM Voucher v")
    Integer findMaxPrice();
}
