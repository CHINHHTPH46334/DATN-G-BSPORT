package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BCTKRepo extends JpaRepository<HoaDon, Integer> {
        @Query(nativeQuery = true, value = "select sum(tong_tien_sau_giam) as [Doanh thu] from hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don where tddh.trang_thai = 'Hoàn thành'\n" +
                "and cast(tddh.ngay_chuyen as date)  between :startDate AND :endDate")
        BigDecimal getDoanhThu(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = "select count(hd.id_hoa_don) as [Đơn hàng] from hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "where tddh.trang_thai = N'Hoàn thành' and cast(tddh.ngay_chuyen as date) BETWEEN :startDate AND :endDate")
        Integer getTongDonHang(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = "select sum(hdct.so_luong) as [Sản phẩm] from hoa_don_chi_tiet hdct \n" +
                "join hoa_don hd on hdct.id_hoa_don = hd.id_hoa_don\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don where tddh.trang_thai = N'Hoàn thành'\n" +
                "and cast(tddh.ngay_chuyen as date) BETWEEN :startDate AND :endDate")
        Integer getTongSanPham(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        //
        @Query(nativeQuery = true, value = "select top 3 hdct.id_chi_tiet_san_pham ,sp.ma_san_pham, sp.ten_san_pham, sum(hdct.so_luong) as so_luong, ctsp.gia_ban from hoa_don hd " +
                "join hoa_don_chi_tiet hdct on hdct.id_hoa_don = hd.id_hoa_don\n" +
                "join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham\n" +
                "join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "where tddh.trang_thai = N'Hoàn thành'\n" +
                "group by sp.ma_san_pham, sp.ten_san_pham, ctsp.gia_ban, hdct.id_chi_tiet_san_pham \n" +
                "order by so_luong desc")
//                and cast(tddh.ngay_chuyen as date) between :startDate and :endDate order by hdct.so_luong desc")
        List<HoaDonResponse> topSanPhamBanChay();

        //
//        @Query(nativeQuery = true, value = "select top 3 hdct.id_chi_tiet_san_pham ,sp.ma_san_pham, sp.ten_san_pham, sum(hdct.so_luong) as so_luong, ctsp.gia_ban from hoa_don hd " +
//                "join hoa_don_chi_tiet hdct on hdct.id_hoa_don = hd.id_hoa_don\n" +
//                "join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham\n" +
//                "join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
//                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
//                "where tddh.trang_thai = N'Hoàn thành'\n" +
//                "group by sp.ma_san_pham, sp.ten_san_pham, ctsp.gia_ban, hdct.id_chi_tiet_san_pham \n" +
//                "order by so_luong desc")
////                and cast(tddh.ngay_chuyen as date) between :startDate and :endDate order by hdct.so_luong desc")
//        List<HoaDonResponse> topSanPhamBanChay();

        @Query(nativeQuery = true, value = "select top 3 hdct.id_chi_tiet_san_pham ,sp.ma_san_pham, sp.ten_san_pham, sum(hdct.so_luong) as so_luong, ctsp.gia_ban from hoa_don hd\n" +
                "join hoa_don_chi_tiet hdct on hdct.id_hoa_don = hd.id_hoa_don\n" +
                "join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham\n" +
                "join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "where tddh.trang_thai = N'Hoàn thành' and cast(tddh.ngay_chuyen as date) between :startDate AND :endDate \n" +
                "group by sp.ma_san_pham, sp.ten_san_pham, ctsp.gia_ban, hdct.id_chi_tiet_san_pham\n" +
                "order by so_luong desc")
        List<HoaDonResponse> topSanPhamBanChay(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = "select top 3 hdct.id_chi_tiet_san_pham ,sp.ma_san_pham, sp.ten_san_pham, sum(hdct.so_luong) as so_luong, ctsp.gia_ban from hoa_don hd\n" +
                "join hoa_don_chi_tiet hdct on hdct.id_hoa_don = hd.id_hoa_don\n" +
                "join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham\n" +
                "join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "where tddh.trang_thai = N'Hoàn thành' and cast(tddh.ngay_chuyen as date) between :startDate AND :endDate \n" +
                "group by sp.ma_san_pham, sp.ten_san_pham, ctsp.gia_ban, hdct.id_chi_tiet_san_pham\n" +
                "order by so_luong asc")
        List<HoaDonResponse> topSanPhamBanCham(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = "SELECT tddh.trang_thai as trangThaiDonHang,\n" +
                "    COUNT(*) as [Số lượng đơn hàng],\n" +
                "    CAST(COUNT(*) AS FLOAT) / (SELECT COUNT(*) FROM  hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don) * 100 as tiLeTrangThaiDonHang\n" +
                "FROM hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "GROUP BY tddh.trang_thai;")
        List<HoaDonResponse> tiLeTrangThaiHoaDon();
}
