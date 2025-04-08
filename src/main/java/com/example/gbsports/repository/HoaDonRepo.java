package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.response.HoaDonResponse;
import com.example.gbsports.response.TheoDoiDonHangResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    @Query(nativeQuery = true, value = """
            SELECT DISTINCT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.email, hd.sdt_nguoi_nhan, hd.trang_thai AS trang_thai_thanh_toan, hd.loai_hoa_don,
                            hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, tdh.trang_thai,
                            hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang
            FROM hoa_don hd
            LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
            LEFT JOIN (SELECT t.id_hoa_don, t.trang_thai
                        FROM theo_doi_don_hang t
                        WHERE t.ngay_chuyen = (SELECT MAX(ngay_chuyen)
                                                FROM theo_doi_don_hang t2
                                                WHERE t2.id_hoa_don = t.id_hoa_don
                                                )
                    ) tdh ON hd.id_hoa_don = tdh.id_hoa_don
            ORDER BY hd.ngay_tao DESC
            """)
    Page<HoaDonResponse> getAllHD(Pageable pageable);

    @Query(value = """
            SELECT DISTINCT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.email, hd.sdt_nguoi_nhan,hd.trang_thai AS trang_thai_thanh_toan, hd.loai_hoa_don,
                            hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, tdh.trang_thai,
                            hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang
            FROM hoa_don hd
            JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id_nhan_vien
            LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
            LEFT JOIN (SELECT t.id_hoa_don, t.trang_thai
                        FROM theo_doi_don_hang t
                        WHERE t.ngay_chuyen = (SELECT MAX(ngay_chuyen)
                                                FROM theo_doi_don_hang t2
                                                WHERE t2.id_hoa_don = t.id_hoa_don
                                                )
                        ) tdh ON hd.id_hoa_don = tdh.id_hoa_don
            WHERE (:keyword IS NULL OR hd.ma_hoa_don LIKE %:keyword%)
            OR (:keyword IS NULL OR nv.ma_nhan_vien LIKE %:keyword%)
            OR (:keyword IS NULL OR hd.sdt_nguoi_nhan LIKE %:keyword%)
            ORDER BY hd.ngay_tao DESC
            """, nativeQuery = true)
    Page<HoaDonResponse> timHoaDon(
            @Param("keyword") String keyword,
            Pageable pageable);

    // Lọc theo khoảng ngày
    @Query(value = """
            SELECT DISTINCT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.email, hd.sdt_nguoi_nhan,hd.trang_thai AS trang_thai_thanh_toan, hd.loai_hoa_don,
                            hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, tdh.trang_thai,
                            hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang
            FROM hoa_don hd
            LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
            LEFT JOIN (SELECT t.id_hoa_don, t.trang_thai
                        FROM theo_doi_don_hang t
                        WHERE t.ngay_chuyen = (SELECT MAX(ngay_chuyen)
                                                FROM theo_doi_don_hang t2
                                                WHERE t2.id_hoa_don = t.id_hoa_don
                                                )
                        ) tdh ON hd.id_hoa_don = tdh.id_hoa_don
            WHERE hd.ngay_tao BETWEEN :tuNgay AND :denNgay
            ORDER BY hd.ngay_tao DESC
            """, nativeQuery = true)
    Page<HoaDonResponse> findHoaDonByNgay(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable);

    // Lọc theo trạng thái theo dõi đơn hàng
    @Query(value = """
            SELECT DISTINCT hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten, hd.sdt_nguoi_nhan,hd.trang_thai AS trang_thai_thanh_toan, hd.loai_hoa_don,
                            hd.dia_chi, v.ma_voucher, hd.tong_tien_sau_giam, tdh.trang_thai,
                            hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang
            FROM hoa_don hd
            LEFT JOIN voucher v ON hd.id_voucher = v.id_voucher
            LEFT JOIN (SELECT t.id_hoa_don, t.trang_thai
                        FROM theo_doi_don_hang t
                        WHERE t.ngay_chuyen = (SELECT MAX(ngay_chuyen)
                                                FROM theo_doi_don_hang t2
                                                WHERE t2.id_hoa_don = t.id_hoa_don
                                                )
                        ) tdh ON hd.id_hoa_don = tdh.id_hoa_don
            WHERE tdh.trang_thai = :trangThai
            ORDER BY hd.ngay_tao DESC
            """, nativeQuery = true)
    Page<HoaDonResponse> findHoaDonByTrangThaiGiaoHang(
            @Param("trangThai") String trangThai,
            Pageable pageable);

    @Query(value = """
            SELECT hd.id_hoa_don, hd.ma_hoa_don, hd.ngay_tao, hd.email, hd.ho_ten, hd.sdt_nguoi_nhan,
            hd.dia_chi, v.ma_voucher, hd.tong_tien_truoc_giam, hd.tong_tien_sau_giam, nv.ten_nhan_vien,
            hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang, hd.id_voucher, hd.phi_van_chuyen, v.mo_ta,
            hd.trang_thai AS trang_thai_thanh_toan, hd.loai_hoa_don, hd.ghi_chu,
            (SELECT TOP 1 trang_thai FROM theo_doi_don_hang t
            WHERE t.id_hoa_don = hd.id_hoa_don
            ORDER BY t.ngay_chuyen DESC) as trang_thai,
            (SELECT TOP 1 ngay_chuyen FROM theo_doi_don_hang t
            WHERE t.id_hoa_don = hd.id_hoa_don
            ORDER BY t.ngay_chuyen DESC) as ngay_chuyen
            FROM hoa_don hd
            FULL OUTER JOIN voucher v ON hd.id_voucher = v.id_voucher
            FULL OUTER JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id_nhan_vien
            WHERE hd.ma_hoa_don = :maHoaDon""", nativeQuery = true)
    Optional<HoaDonResponse> findByMaHoaDon(@Param("maHoaDon") String maHoaDon);

    @Query(value = """
                INSERT INTO theo_doi_don_hang (id_hoa_don, trang_thai, ngay_chuyen)
                SELECT id_hoa_don, :newTrangThai, :ngayChuyen
                FROM hoa_don
                WHERE ma_hoa_don = :maHoaDon
            """, nativeQuery = true)
    @Modifying
    @Transactional
    void insertTrangThaiDonHang(@Param("maHoaDon") String maHoaDon,
                                @Param("newTrangThai") String newTrangThai,
                                @Param("ngayChuyen") LocalDateTime ngayChuyen);

    // Lấy trạng thái mới nhất
    @Query(value = """
                SELECT trang_thai, ngay_chuyen
                FROM theo_doi_don_hang
                WHERE id_hoa_don = :idHoaDon
                ORDER BY ngay_chuyen ASC
            """, nativeQuery = true)
    List<TheoDoiDonHangResponse> findTrangThaiHistoryByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    @Query(value = """
            select id_hoa_don, ma_hoa_don, hd.id_nhan_vien, ten_nhan_vien, hd.id_khach_hang, ten_khach_hang, hd.trang_thai,\s
            hd.id_voucher, ten_voucher, sdt_nguoi_nhan, dia_chi, hd.email, tong_tien_truoc_giam, phi_van_chuyen, ho_ten,
            tong_tien_sau_giam, hinh_thuc_thanh_toan, phuong_thuc_nhan_hang, loai_hoa_don, ghi_chu, hd.ngay_tao
            from hoa_don hd\s
            full outer join khach_hang kh on kh.id_khach_hang = hd.id_khach_hang
            full outer join nhan_vien nv on nv.id_nhan_vien = hd.id_nhan_vien
            full outer join voucher vc on vc.id_voucher = hd.id_voucher
            where hd.trang_thai = N'Chưa thanh toán'
            """, nativeQuery = true)
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
    List<HoaDonResponse> findHoaDonById(@Param("idHd") Integer idHd);

    @Query(value = """
                SELECT * FROM hoa_don
            """, nativeQuery = true)
    List<HoaDonResponse> getListHD();

    @Query(value = """
            SELECT TOP 1 trang_thai
            FROM theo_doi_don_hang
            WHERE id_hoa_don = :idHoaDon
              AND trang_thai != N'Đã cập nhật'
            ORDER BY ngay_chuyen DESC
            """, nativeQuery = true)
    String findLatestNonUpdatedStatusByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);
}
