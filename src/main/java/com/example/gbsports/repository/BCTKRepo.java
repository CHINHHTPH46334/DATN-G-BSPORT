package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BCTKRepo extends JpaRepository<HoaDon, Integer> {
        @Query(nativeQuery = true,value = "select sum(tong_tien_sau_giam) as [Doanh thu] from hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don where tddh.trang_thai = 'Hoàn thành'\n" +
                "and cast(tddh.ngay_chuyen as date)  between :startDate AND :endDate")
        BigDecimal getDoanhThu(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true,value = "select count(hd.id_hoa_don) as [Đơn hàng] from hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "where tddh.trang_thai = N'Hoàn thành' and cast(tddh.ngay_chuyen as date) BETWEEN :startDate AND :endDate")
        Integer getTongDonHang(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true,value = "select sum(hdct.so_luong) as [Sản phẩm] from hoa_don_chi_tiet hdct \n" +
                "join hoa_don hd on hdct.id_hoa_don = hd.id_hoa_don\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don where tddh.trang_thai = N'Hoàn thành'\n" +
                "and cast(tddh.ngay_chuyen as date) BETWEEN :startDate AND :endDate")
        Integer getTongSanPham(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
