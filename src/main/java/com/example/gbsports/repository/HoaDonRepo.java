package com.example.gbsports.repository;


import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    @Query(nativeQuery = true, value = "SELECT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.sdt_nguoi_nhan, " +
            "hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, td.trang_thai, " +
            "hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang " +
            "FROM hoa_don hd " +
            "LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher " +
            "LEFT JOIN theo_doi_don_hang td ON hd.id_hoa_don = td.id_hoa_don")
    Page<HoaDonResponse> getAllHD(Pageable pageable);

    @Query(value = """
                SELECT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.sdt_nguoi_nhan, 
                       hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, td.trang_thai, 
                       hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang 
                FROM hoa_don hd
                JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id_nhan_vien
                LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
                LEFT JOIN theo_doi_don_hang td ON hd.id_hoa_don = td.id_hoa_don
                WHERE (:keyword IS NULL OR hd.ma_hoa_don LIKE %:keyword%)
                OR (:keyword IS NULL OR nv.ma_nhan_vien LIKE %:keyword%)
                OR (:keyword IS NULL OR hd.sdt_nguoi_nhan LIKE %:keyword%)
            """, nativeQuery = true)
    Page<HoaDonResponse> timHoaDon(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //Lọc theo khoảng ngày
    @Query(value = """
                SELECT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.sdt_nguoi_nhan, 
                       hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, td.trang_thai, 
                       hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang 
                FROM hoa_don hd
                LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
                LEFT JOIN theo_doi_don_hang td ON hd.id_hoa_don = td.id_hoa_don
                WHERE hd.ngay_tao BETWEEN :tuNgay AND :denNgay
            """, nativeQuery = true)
    Page<HoaDonResponse> findHoaDonByNgay(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );

    // Lọc theo trạng thái theo dõi đơn hàng
    @Query(value = """
                SELECT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.sdt_nguoi_nhan, 
                       hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, tdh.trang_thai, 
                       hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang 
                FROM hoa_don hd
                JOIN theo_doi_don_hang tdh ON hd.id_hoa_don = tdh.id_hoa_don
                LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
                WHERE tdh.trang_thai = :trangThai
            """, nativeQuery = true)
    Page<HoaDonResponse> findHoaDonByTrangThaiGiaoHang(
            @Param("trangThai") String trangThai,
            Pageable pageable
    );

    @Query("SELECT h FROM HoaDon h WHERE h.ma_hoa_don = :maHoaDon")
    Optional<HoaDon> findByMaHoaDon(@Param("maHoaDon") String maHoaDon);
}
