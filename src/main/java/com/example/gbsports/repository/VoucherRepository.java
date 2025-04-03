package com.example.gbsports.repository;

import com.example.gbsports.entity.Voucher;
import com.example.gbsports.response.VoucherBHResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

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

    // @Query("SELECT COALESCE(MAX(v.giaTriToiDa), 0) FROM Voucher v")
    // BigDecimal findMaxPrice();

    @Query(value = """
    SELECT
        hd.id_hoa_don,
        v.id_voucher,
        CASE\s
            WHEN v.kieu_giam_gia = N'Phần trăm' THEN\s
                CASE\s
                    WHEN (hd.tong_tien_truoc_giam * v.gia_tri_giam / 100) >= COALESCE(v.gia_tri_toi_da, hd.tong_tien_truoc_giam)\s
                    THEN COALESCE(v.gia_tri_toi_da, hd.tong_tien_truoc_giam)
                    ELSE (hd.tong_tien_truoc_giam * v.gia_tri_giam / 100)
                END
            WHEN v.kieu_giam_gia = N'Tiền mặt' THEN\s
                CASE\s
                    WHEN hd.tong_tien_truoc_giam > COALESCE(v.gia_tri_giam, 0)\s
                    THEN COALESCE(v.gia_tri_giam, 0)
                    ELSE 0
                END
            ELSE 0
        END AS GiaTriGiamThucTe
    FROM hoa_don hd
    CROSS JOIN voucher v
    WHERE v.trang_thai = N'Đang diễn ra'
    AND hd.trang_thai = N'Chưa thanh toán'
    AND v.gia_tri_toi_thieu <= hd.tong_tien_truoc_giam
    AND hd.id_hoa_don = :idHD
    ORDER BY GiaTriGiamThucTe DESC
    """, nativeQuery = true)
    List<VoucherBHResponse> giaTriGiamThucTeByIDHD(@RequestParam("idHD") Integer idHD);

    @Query("SELECT MAX(v.giaTriToiDa) FROM Voucher v")
    Integer findMaxPrice();
}
