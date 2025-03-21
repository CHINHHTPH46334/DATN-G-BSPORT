package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.response.HoaDonChiTietResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet, Integer> {
    @Query(value = """
                SELECT DISTINCT hd.id_hoa_don, hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten,hd.sdt_nguoi_nhan, 
                    hd.dia_chi, hd.email, hd.tong_tien_truoc_giam, hd.phi_van_chuyen, 
                    hd.tong_tien_sau_giam, hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang, 
                    tdh.trang_thai, hdct.id_hoa_don_chi_tiet, hdct.so_luong, hdct.don_gia, 
                    sp.ten_san_pham, sp.ma_san_pham, 
                    kt.gia_tri AS kich_thuoc, 
                    ms.ten_mau_sac, 
                    ha.hinh_anh, ha.anh_chinh
                FROM hoa_don hd
                JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don
                JOIN chi_tiet_san_pham ctsp ON hdct.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
                JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham
                JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id_kich_thuoc
                JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id_mau_sac
                LEFT JOIN (SELECT t.id_hoa_don, t.trang_thai
                            FROM theo_doi_don_hang t
                            WHERE t.ngay_chuyen = (SELECT MAX(ngay_chuyen)
                                                    FROM theo_doi_don_hang t2
                                                    WHERE t2.id_hoa_don = t.id_hoa_don
                                                    )
                            ) tdh ON hd.id_hoa_don = tdh.id_hoa_don
                LEFT JOIN hinh_anh ha ON ctsp.id_chi_tiet_san_pham = ha.id_chi_tiet_san_pham AND ha.anh_chinh = 1
                WHERE hd.id_hoa_don = :idHoaDon
            """, nativeQuery = true)
    List<HoaDonChiTietResponse> findHoaDonChiTietById(
            @Param("idHoaDon") Integer idHoaDon
    );

    @Modifying
    @Transactional
    @Query(value = """
            BEGIN TRANSACTION;
            	
            	DECLARE @SOLUONG INT = :soLuong;
            	DECLARE @IDCTSP INT = :idCTSP;
            	DECLARE @IDHD INT = :idHoaDon;
            	
            	UPDATE hoa_don_chi_tiet
                SET
                    so_luong = so_luong + @SOLUONG,
                    don_gia = (so_luong + @SOLUONG) * (SELECT gia_ban FROM chi_tiet_san_pham WHERE id_chi_tiet_san_pham = @IDCTSP)
                WHERE id_chi_tiet_san_pham = @IDCTSP
                AND id_hoa_don = @IDHD;
                
            	UPDATE chi_tiet_san_pham\s
            	SET\s
            		so_luong = so_luong - @SOLUONG
            	WHERE id_chi_tiet_san_pham = @IDCTSP;
                
            	UPDATE hoa_don
            	SET
            		tong_tien_truoc_giam = phi_van_chuyen + (SELECT TOP 1 SUM(don_gia) FROM hoa_don_chi_tiet hdct WHERE hdct.id_hoa_don = @IDHD),
            		tong_tien_sau_giam = phi_van_chuyen +\s
            		(SELECT TOP 1 SUM(don_gia) FROM hoa_don_chi_tiet hdct WHERE hdct.id_hoa_don = @IDHD) -
            		(SELECT COALESCE((
            			SELECT vc.gia_tri_toi_da\s
            			FROM hoa_don hd\s
            			LEFT JOIN voucher vc ON vc.id_voucher = hd.id_voucher
            			WHERE hd.tong_tien_truoc_giam >= vc.gia_tri_toi_thieu
            			AND hd.id_hoa_don = @IDHD
            		), 0) AS GiaTriToiDa)
            	WHERE id_hoa_don = @IDHD;
            COMMIT;
            """, nativeQuery = true)
    void addSLGH(@Param("idCTSP") Integer idCTSP, @Param("idHoaDon") Integer idHoaDon, @Param("soLuong") Integer soLuong);

    @Modifying
    @Transactional
    @Query(value = """
            BEGIN TRANSACTION;
                   
                DECLARE @SOLUONG INT = :soLuong;
            	DECLARE @IDCTSP INT = :idCTSP;
            	DECLARE @IDHD INT = :idHoaDon;
                   
                DECLARE @GIABAN DECIMAL(18, 2);
                SELECT @GIABAN = gia_ban FROM chi_tiet_san_pham WHERE id_chi_tiet_san_pham = @IDCTSP;
                   
                DECLARE @PHIVANCHUYEN DECIMAL(18, 2);
                SELECT @PHIVANCHUYEN = phi_van_chuyen FROM hoa_don WHERE id_hoa_don = @IDHD;
                   
                UPDATE hoa_don_chi_tiet
                SET
                    so_luong = so_luong - @SOLUONG,
                    don_gia = (so_luong - @SOLUONG) * @GIABAN
                WHERE id_hoa_don = @IDHD AND id_chi_tiet_san_pham = @IDCTSP;
                   
                DECLARE @TONGTIENTRUOCGIAM DECIMAL(18, 2);
                SELECT @TONGTIENTRUOCGIAM = @PHIVANCHUYEN + SUM(don_gia)
                FROM hoa_don_chi_tiet
                WHERE id_hoa_don = @IDHD
                GROUP BY id_hoa_don;
                   
                DECLARE @GIATRIVOUCHER DECIMAL(18, 2);
                SELECT @GIATRIVOUCHER = COALESCE(vc.gia_tri_toi_da, 0)
                FROM hoa_don hd
                LEFT JOIN voucher vc ON vc.id_voucher = hd.id_voucher
                WHERE hd.tong_tien_truoc_giam >= vc.gia_tri_toi_thieu
                    AND hd.id_hoa_don = @IDHD;
                   
                UPDATE hoa_don
                SET
                    tong_tien_truoc_giam = @TONGTIENTRUOCGIAM,
                    tong_tien_sau_giam = @TONGTIENTRUOCGIAM - @GIATRIVOUCHER
                WHERE id_hoa_don = @IDHD;
                   
                UPDATE chi_tiet_san_pham
                SET
                    so_luong = so_luong + @SOLUONG
                WHERE id_chi_tiet_san_pham = @IDCTSP;
                   
            COMMIT;
            """, nativeQuery = true)
    void removeSPGH(@Param("idCTSP") Integer idCTSP, @Param("idHoaDon") Integer idHoaDon, @Param("soLuong") Integer soLuong);

//    @Query(value = """
//
//    """, nativeQuery = true)
//    void addSPHD();

    @Query(value = """
            select top 1 sum(don_gia) from hoa_don_chi_tiet hdct where hdct.id_hoa_don = :idHD
            """, nativeQuery = true)
    BigDecimal getDonGiaTongByIDHD(@Param("idHD") Integer idHD);

    @Query("SELECT h FROM HoaDonChiTiet h WHERE h.chiTietSanPham.id_chi_tiet_san_pham = :idChiTietSanPham AND h.hoaDon.id_hoa_don = :idHoaDon")
    Optional<HoaDonChiTiet> findByChiTietSanPhamIdAndHoaDonId(@Param("idChiTietSanPham") Integer idChiTietSanPham, @Param("idHoaDon") Integer idHoaDon);

    @Query("SELECT COALESCE(SUM(hdct.don_gia), 0) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id = :idHoaDon")
    BigDecimal sumDonGiaByHoaDonId(@Param("idHoaDon") Integer idHoaDon);

    @Query(value = """
    select hdct.id_hoa_don_chi_tiet, ctsp.id_chi_tiet_san_pham, sp.ma_san_pham, sp.ten_san_pham, ha.hinh_anh, hdct.so_luong, ctsp.gia_ban, hdct.don_gia
    from hoa_don_chi_tiet hdct
    left join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham
    left join san_pham sp on sp.id_san_pham = ctsp.id_san_pham
    left join hinh_anh ha on ha.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
    where hdct.id_hoa_don = :idHD
    """, nativeQuery = true)
    List<HoaDonChiTietResponse> getSPGH(Integer idHD);
}
