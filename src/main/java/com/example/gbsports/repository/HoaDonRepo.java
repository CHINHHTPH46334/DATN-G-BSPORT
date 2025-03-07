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

    @Query(value = "select * from hoa_don where trang_thai like N'Chưa thanh toán'", nativeQuery = true)
    List<HoaDonResponse> getAllHoaDonCTT();

    @Query(value = """
            SELECT id_hoa_don,ma_hoa_don,nv.id_nhan_vien,nv.ten_nhan_vien,kh.id_khach_hang,kh.ten_khach_hang,hd.ngay_tao,hd.ngay_sua,hd.trang_thai
            ,vc.id_voucher,vc.ten_voucher,sdt_nguoi_nhan,dia_chi,hd.email,tong_tien_truoc_giam,phi_van_chuyen,ho_ten
            ,tong_tien_sau_giam,hinh_thuc_thanh_toan,phuong_thuc_nhan_hang
            from hoa_don hd
            LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id_nhan_vien
            LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id_khach_hang
            LEFT JOIN voucher vc ON hd.id_voucher = vc.id_voucher
            where hd.id_hoa_don = :idHd
""", nativeQuery = true)
    List<HoaDonResponse> findHoaDonById(Integer idHd);

}
