package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BCTKRepo extends JpaRepository<HoaDon, Integer> {
        @Query(nativeQuery = true, value = "SELECT \n" +
                "    SUM(hd.tong_tien_sau_giam) - ISNULL(SUM(th.tong_tien_hoan), 0) AS [Doanh thu] " +
                "FROM hoa_don hd\n" +
                "JOIN theo_doi_don_hang tddh ON hd.id_hoa_don = tddh.id_hoa_don\n" +
                "LEFT JOIN tra_hang th ON hd.id_hoa_don = th.id_hoa_don\n" +
                "WHERE tddh.trang_thai = N'Hoàn thành'\n" +
                "  AND CAST(tddh.ngay_chuyen AS DATE) BETWEEN :startDate AND :endDate")
        BigDecimal getDoanhThu(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = "select count(hd.id_hoa_don) as [Đơn hàng] from hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "where tddh.trang_thai = N'Hoàn thành' and cast(tddh.ngay_chuyen as date) BETWEEN :startDate AND :endDate")
        Integer getTongDonHang(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = "SELECT \n" +
                "    SUM(hdct.so_luong) \n" +
                "    - ISNULL((\n" +
                "        SELECT SUM(ctth.so_luong)\n" +
                "        FROM tra_hang th\n" +
                "        JOIN chi_tiet_tra_hang ctth ON th.id = ctth.id_tra_hang\n" +
                "        WHERE th.trang_thai = N'Trả hàng'\n" +
                "          AND CAST(th.ngay_tao AS DATE) BETWEEN :startDate AND :endDate\n" +
                "    ), 0) AS so_luong_ban_thuc_te\n" +
                "FROM hoa_don hd\n" +
                "JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don\n" +
                "JOIN theo_doi_don_hang tddh ON hd.id_hoa_don = tddh.id_hoa_don\n" +
                "WHERE tddh.trang_thai = N'Hoàn thành'\n" +
                "  AND CAST(tddh.ngay_chuyen AS DATE) BETWEEN :startDate AND :endDate")
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

        @Query(nativeQuery = true, value = """
            select top 5 hdct.id_chi_tiet_san_pham ,sp.ma_san_pham, (sp.ten_san_pham+N': Màu: '+ms.ten_mau_sac+N', size: '+kt.gia_tri) as ten_san_pham, sum(hdct.so_luong) as so_luong, ctsp.gia_ban from hoa_don hd
            join hoa_don_chi_tiet hdct on hdct.id_hoa_don = hd.id_hoa_don
            join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham
            join san_pham sp on sp.id_san_pham = ctsp.id_san_pham
            join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don
            join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac
            join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc
            where tddh.trang_thai = N'Hoàn thành' and cast(tddh.ngay_chuyen as date) between :startDate AND :endDate
            group by sp.ma_san_pham, sp.ten_san_pham, ctsp.gia_ban, hdct.id_chi_tiet_san_pham, ms.ten_mau_sac, kt.gia_tri
             order by so_luong desc
            """)
        List<HoaDonResponse> topSanPhamBanChay(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query(nativeQuery = true, value = """
            select ctsp.id_chi_tiet_san_pham ,sp.ma_san_pham, (sp.ten_san_pham+N': Màu: '+ms.ten_mau_sac+N', size: '+kt.gia_tri) as ten_san_pham, 
            ctsp.so_luong, ctsp.gia_ban from san_pham sp
            join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham
            join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac
            join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc
            where ctsp.so_luong < 10
            order by so_luong asc
                        """)
        List<ChiTietSanPhamView> topSanPhamSapHetHang();

        @Query(nativeQuery = true, value = "SELECT tddh.trang_thai as trangThaiDonHang,\n" +
                "    COUNT(*) as [Số lượng đơn hàng],\n" +
                "    CAST(COUNT(*) AS FLOAT) / (SELECT COUNT(*) FROM  hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don) * 100 as tiLeTrangThaiDonHang\n" +
                "FROM hoa_don hd\n" +
                "join theo_doi_don_hang tddh on tddh.id_hoa_don = hd.id_hoa_don\n" +
                "GROUP BY tddh.trang_thai;")
        List<HoaDonResponse> tiLeTrangThaiHoaDon();
}
