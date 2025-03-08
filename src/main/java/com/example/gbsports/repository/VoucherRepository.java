package com.example.gbsports.repository;

import com.example.gbsports.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface VoucherRepository extends JpaRepository<Voucher,Integer> {
    @Query("SELECT v FROM Voucher v WHERE " +
            "v.maVoucher LIKE %:keyword% OR v.tenVoucher LIKE %:keyword%")
    Page<Voucher> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    Page<Voucher> findByTrangThai(String trangThai, Pageable pageable);
    boolean existsByMaVoucher(String maVoucher);
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:startDate IS NULL OR v.ngayBatDau >= :startDate) AND " +
            "(:endDate IS NULL OR v.ngayHetHan <= :endDate)")
    Page<Voucher> searchByDateRange(@Param("startDate") LocalDateTime  startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.giaTriToiDa BETWEEN :minPrice AND :maxPrice")
    Page<Voucher> searchByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);

    @Query("SELECT MIN(v.giaTriToiDa) FROM Voucher v")
    Integer findMinPrice();

    @Query("SELECT MAX(v.giaTriToiDa) FROM Voucher v")
    Integer findMaxPrice();
}
