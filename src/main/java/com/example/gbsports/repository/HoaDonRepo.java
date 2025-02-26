package com.example.gbsports.repository;


import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    @Query(nativeQuery = true, value = "select id_hoa_don, ma_hoa_don, ngay_tao, ngay_sua, trang_thai, sdt_nguoi_nhan, ho_ten, dia_chi, email, tong_tien_truoc_giam, phi_van_chuyen, tong_tien_sau_giam, hinh_thuc_thanh_toan, phuong_thuc_nhan_hang from hoa_don")
    Page<HoaDonResponse> phanTrang(Pageable pageable);

    @Query(value = """
                SELECT hd.*
                FROM hoa_don hd
                JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id_nhan_vien
                WHERE :keyword IS NULL OR hd.ma_hoa_don LIKE %:keyword%
                OR :keyword IS NULL OR nv.ma_nhan_vien LIKE %:keyword%
                OR :keyword IS NULL OR hd.sdt_nguoi_nhan LIKE %:keyword%
            """, nativeQuery = true)
    Page<HoaDon> timHoaDon(
            @Param("keyword") String keyword, Pageable pageable
//            @Param("maNhanVien") String maNhanVien,
//            @Param("sdtNguoiNhan") String sdtNguoiNhan
    );

    //Lọc theo khoảng ngày
    @Query(value = """
                SELECT hd.* 
                FROM hoa_don hd
                WHERE hd.ngay_tao BETWEEN :tuNgay AND :denNgay
            """, nativeQuery = true)
    List<HoaDonResponse> findHoaDonByNgay(
            @Param("tuNgay") Date tuNgay,
            @Param("denNgay") Date denNgay
    );

    // Lọc theo trạng thái theo dõi đơn hàng
    @Query(value = """
                SELECT hd.*
                FROM hoa_don hd
                JOIN theo_doi_don_hang tdh ON hd.id_hoa_don = tdh.id_hoa_don
                WHERE tdh.trang_thai = ?1
            """, nativeQuery = true)
    List<HoaDonResponse> findHoaDonByTrangThaiGiaoHang(@Param("trangThai") String trangThai);

}
