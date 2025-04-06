package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.response.HoaDonChiTietResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Query("SELECT COALESCE(SUM(hdct.don_gia), 0) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id_hoa_don = :idHoaDon")
    BigDecimal sumDonGiaByHoaDonId(@Param("idHoaDon") Integer idHoaDon);

    @Query(value = """
            SELECT\s
                hdct.id_hoa_don_chi_tiet,
                hdct.id_hoa_don,
                ctsp.id_chi_tiet_san_pham,
                sp.ma_san_pham,
                sp.ten_san_pham,
                ha.hinh_anh,
                hdct.so_luong,
                ctsp.so_luong AS so_luong_ton,
                COALESCE(
                    (
                        SELECT\s
                            CASE\s
                                WHEN km.kieu_giam_gia = N'Phần trăm' AND km.trang_thai = N'Đang diễn ra' THEN
                                    IIF(
                                        ctsp.gia_ban - IIF(
                                            (ctsp.gia_ban * COALESCE(km.gia_tri_giam, 0) / 100) > COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            (ctsp.gia_ban * COALESCE(km.gia_tri_giam, 0) / 100)
                                        ) < 0,
                                        0,
                                        ctsp.gia_ban - IIF(
                                            (ctsp.gia_ban * COALESCE(km.gia_tri_giam, 0) / 100) > COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            (ctsp.gia_ban * COALESCE(km.gia_tri_giam, 0) / 100)
                                        )
                                    )
                                WHEN km.kieu_giam_gia = N'Tiền mặt' AND km.trang_thai = N'Đang diễn ra' THEN
                                    IIF(
                                        ctsp.gia_ban - IIF(
                                            COALESCE(km.gia_tri_giam, 0) > COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            COALESCE(km.gia_tri_giam, 0)
                                        ) < 0,
                                        0,
                                        ctsp.gia_ban - IIF(
                                            COALESCE(km.gia_tri_giam, 0) > COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            COALESCE(km.gia_tri_toi_da, ctsp.gia_ban),
                                            COALESCE(km.gia_tri_giam, 0)
                                        )
                                    )
                                ELSE ctsp.gia_ban
                            END
                        FROM chi_tiet_khuyen_mai ctkm
                        FULL OUTER JOIN khuyen_mai km ON km.id_khuyen_mai = ctkm.id_khuyen_mai
                        WHERE ctkm.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
                    ),
                    ctsp.gia_ban
                ) AS gia_ban,
                hdct.don_gia,
                ms.ten_mau_sac,
                kt.gia_tri
            FROM hoa_don_chi_tiet hdct
            FULL OUTER JOIN chi_tiet_san_pham ctsp ON ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham
            FULL OUTER JOIN san_pham sp ON sp.id_san_pham = ctsp.id_san_pham
            FULL OUTER JOIN hinh_anh ha ON ha.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
            FULL OUTER JOIN kich_thuoc kt ON kt.id_kich_thuoc = ctsp.id_kich_thuoc
            FULL OUTER JOIN mau_sac ms ON ms.id_mau_sac = ctsp.id_mau_sac
            FULL OUTER JOIN chat_lieu cl ON cl.id_chat_lieu = sp.id_chat_lieu
            WHERE hdct.id_hoa_don = :idHD AND (ha.anh_chinh = 1 OR ha.anh_chinh IS NULL)
            """, nativeQuery = true)
    List<HoaDonChiTietResponse> getSPGH(Integer idHD);

    @Modifying
    @Transactional
    @Query(value = """
            BEGIN TRANSACTION;
                
            -- Khai báo các biến
            DECLARE @SOLUONG INT = :soLuong; -- Số lượng sản phẩm cần thêm
            DECLARE @IDCTSP INT = :idCTSP;  -- ID chi tiết sản phẩm
            DECLARE @IDHD INT = :idHD;   -- ID hóa đơn
                
            -- Khai báo biến để tìm voucher tốt nhất và tổng tiền trước giảm
            DECLARE @TongTienTruocGiam DECIMAL(12,2);
            DECLARE @GiaTriGiamVoucher DECIMAL(12,2); -- Biến để lưu giá trị giảm từ voucher
                
            SELECT @GiaTriGiamVoucher = (
            	select vc.gia_tri_giam from hoa_don hd
            	full outer join voucher vc on vc.id_voucher = hd.id_voucher
            	where hd.id_hoa_don = @IDHD
            )
                
                
            -- Kiểm tra xem hóa đơn có tồn tại không
            IF NOT EXISTS (SELECT 1 FROM hoa_don WHERE id_hoa_don = @IDHD)
            BEGIN
                PRINT N'Hóa đơn không tồn tại!';
                ROLLBACK;
                RETURN;
            END;
                
            -- Tính giá sau khi áp dụng khuyến mãi cho sản phẩm
            DECLARE @GiaSauGiam DECIMAL(12,2);
            SELECT @GiaSauGiam = :giaBan
                
            -- Kiểm tra xem sản phẩm đã tồn tại trong chi tiết hóa đơn chưa
            IF EXISTS (
                SELECT 1\s
                FROM hoa_don_chi_tiet\s
                WHERE id_hoa_don = @IDHD\s
                AND id_chi_tiet_san_pham = @IDCTSP
            )
            BEGIN
                -- Nếu đã tồn tại, cập nhật số lượng và đơn giá
                UPDATE hoa_don_chi_tiet
                SET
                    so_luong = so_luong + @SOLUONG,
                    don_gia = (so_luong + @SOLUONG) * @GiaSauGiam
                WHERE id_chi_tiet_san_pham = @IDCTSP
                AND id_hoa_don = @IDHD;
            END
            ELSE
            BEGIN
                -- Nếu chưa tồn tại, thêm mới chi tiết sản phẩm vào hóa đơn
                INSERT INTO hoa_don_chi_tiet (id_hoa_don, id_chi_tiet_san_pham, so_luong, don_gia)
                VALUES (@IDHD, @IDCTSP, @SOLUONG, @SOLUONG * @GiaSauGiam);
            END;
                
            -- Cập nhật số lượng tồn kho
            UPDATE chi_tiet_san_pham
            SET so_luong = so_luong - @SOLUONG
            WHERE id_chi_tiet_san_pham = @IDCTSP;
                
            -- Tính tổng tiền trước giảm sau khi thêm sản phẩm
            SELECT @TongTienTruocGiam = hd.phi_van_chuyen + ISNULL(SUM(hdct.don_gia), 0)
            FROM hoa_don hd
            LEFT JOIN hoa_don_chi_tiet hdct ON hdct.id_hoa_don = hd.id_hoa_don
            WHERE hd.id_hoa_don = @IDHD
            GROUP BY hd.id_hoa_don, hd.phi_van_chuyen;
                
            -- Cập nhật tổng tiền hóa đơn với áp dụng voucher
            UPDATE hoa_don
            SET
                tong_tien_truoc_giam = @TongTienTruocGiam,
                tong_tien_sau_giam = @TongTienTruocGiam - ISNULL(@GiaTriGiamVoucher, 0)
            WHERE id_hoa_don = @IDHD;
                
            -- Kiểm tra kết quả
            SELECT\s
                hd.id_hoa_don,\s
                hd.id_voucher,\s
                hd.tong_tien_truoc_giam,\s
                hd.tong_tien_sau_giam\s
            FROM hoa_don hd\s
            WHERE hd.id_hoa_don = @IDHD;
                
            COMMIT;
            """, nativeQuery = true)
    void addSPHD(@RequestParam(value = "idHoaDon") Integer idHD,
                 @RequestParam(value = "idCTSP") Integer idCTSP,
                 @RequestParam(value = "soLuong") Integer soLuong,
                 @RequestParam(value = "giaBan") Integer giaBan);

    @Modifying
    @Transactional
    @Query(value = """
            BEGIN TRANSACTION;
                
            -- Khai báo các biến
            DECLARE @SOLUONG INT = 1; -- Số lượng sản phẩm cần giảm
            DECLARE @IDCTSP INT = 1;  -- ID chi tiết sản phẩm
            DECLARE @IDHD INT = 1;   -- ID hóa đơn
                
            -- Khai báo biến để tìm voucher tốt nhất và tổng tiền trước giảm
            DECLARE @TongTienTruocGiam DECIMAL(18,2);
            DECLARE @GiaTriGiamVoucher DECIMAL(18,2); -- Biến để lưu giá trị giảm từ voucher
            DECLARE @PHIVANCHUYEN DECIMAL(18,2);
                
            IF NOT EXISTS (SELECT 1 FROM hoa_don WHERE id_hoa_don = @IDHD)
            BEGIN
                PRINT N'Hóa đơn không tồn tại!';
                ROLLBACK;
                RETURN;
            END;
                
            -- Tính giá sau khi áp dụng khuyến mãi cho sản phẩm
            DECLARE @GiaSauGiam DECIMAL(18,2);
                
            SELECT @GiaSauGiam = ( select
                CASE\s
                    WHEN km.kieu_giam_gia = N'Phần trăm' AND km.trang_thai = N'Đang diễn ra' THEN\s
                        IIF(gia_ban - IIF((gia_ban * COALESCE(km.gia_tri_giam, 0) / 100) > COALESCE(km.gia_tri_toi_da, gia_ban),\s
                            COALESCE(km.gia_tri_toi_da, gia_ban),\s
                            (gia_ban * COALESCE(km.gia_tri_giam, 0) / 100)) < 0,\s
                            0,\s
                            gia_ban - IIF((gia_ban * COALESCE(km.gia_tri_giam, 0) / 100) > COALESCE(km.gia_tri_toi_da, gia_ban),\s
                                COALESCE(km.gia_tri_toi_da, gia_ban),\s
                                (gia_ban * COALESCE(km.gia_tri_giam, 0) / 100)))
                    WHEN km.kieu_giam_gia = N'Tiền mặt' AND km.trang_thai = N'Đang diễn ra' THEN\s
                        IIF(gia_ban - IIF(COALESCE(km.gia_tri_giam, 0) > COALESCE(km.gia_tri_toi_da, gia_ban),\s
                            COALESCE(km.gia_tri_toi_da, gia_ban),\s
                            COALESCE(km.gia_tri_giam, 0)) < 0,\s
                            0,\s
                            gia_ban - IIF(COALESCE(km.gia_tri_giam, 0) > COALESCE(km.gia_tri_toi_da, gia_ban),\s
                                COALESCE(km.gia_tri_toi_da, gia_ban),\s
                                COALESCE(km.gia_tri_giam, 0)))
                    ELSE gia_ban
                END AS gia_sau_giam
            FROM chi_tiet_san_pham ctsp
            FULL OUTER JOIN san_pham sp ON sp.id_san_pham = ctsp.id_san_pham
            FULL OUTER JOIN chi_tiet_khuyen_mai ctkm ON ctkm.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
            FULL OUTER JOIN khuyen_mai km ON km.id_khuyen_mai = ctkm.id_khuyen_mai
            WHERE ctsp.trang_thai like N'Hoạt động' AND ctsp.id_chi_tiet_san_pham = @IDCTSP)
                
            -- Lấy phí vận chuyển từ hoa_don
            SELECT @PHIVANCHUYEN = phi_van_chuyen FROM hoa_don WHERE id_hoa_don = @IDHD;
                
            -- Kiểm tra xem sản phẩm đã tồn tại trong chi tiết hóa đơn chưa
            IF EXISTS (
                SELECT 1\s
                FROM hoa_don_chi_tiet\s
                WHERE id_hoa_don = @IDHD\s
                AND id_chi_tiet_san_pham = @IDCTSP
            )
            BEGIN
                -- Kiểm tra số lượng hiện tại đủ để giảm không
                DECLARE @SoLuongHienTai INT;
                SELECT @SoLuongHienTai = so_luong\s
                FROM hoa_don_chi_tiet\s
                WHERE id_hoa_don = @IDHD\s
                AND id_chi_tiet_san_pham = @IDCTSP;
                
                IF @SoLuongHienTai < @SOLUONG
                BEGIN
                    PRINT N'Số lượng trong hóa đơn không đủ để giảm!';
                    ROLLBACK;
                    RETURN;
                END;
                
                -- Cập nhật số lượng và đơn giá trong hoa_don_chi_tiet
                UPDATE hoa_don_chi_tiet
                SET
                    so_luong = so_luong - @SOLUONG,
                    don_gia = (so_luong - @SOLUONG) * @GiaSauGiam
                WHERE id_hoa_don = @IDHD\s
                AND id_chi_tiet_san_pham = @IDCTSP;
                
                -- Nếu số lượng sau khi giảm bằng 0, xóa bản ghi
                DELETE FROM hoa_don_chi_tiet
                WHERE id_hoa_don = @IDHD\s
                AND id_chi_tiet_san_pham = @IDCTSP\s
                AND so_luong = 0;
            END
            ELSE
            BEGIN
                PRINT N'Sản phẩm không tồn tại trong hóa đơn để giảm!';
                ROLLBACK;
                RETURN;
            END;
                
            -- Tính tổng tiền trước giảm sau khi giảm sản phẩm
            SELECT @TongTienTruocGiam = @PHIVANCHUYEN + ISNULL(SUM(don_gia), 0)
            FROM hoa_don hd
            LEFT JOIN hoa_don_chi_tiet hdct ON hdct.id_hoa_don = hd.id_hoa_don
            WHERE hd.id_hoa_don = @IDHD
            GROUP BY hd.id_hoa_don, hd.phi_van_chuyen;
                
            -- Cập nhật tổng tiền trong hoa_don
            UPDATE hoa_don
            SET
                tong_tien_truoc_giam = @TongTienTruocGiam,
                tong_tien_sau_giam = @TongTienTruocGiam
            WHERE id_hoa_don = @IDHD;
                
            -- Cập nhật số lượng trong chi_tiet_san_pham
            UPDATE chi_tiet_san_pham
            SET
                so_luong = so_luong + @SOLUONG
            WHERE id_chi_tiet_san_pham = @IDCTSP;
                
                
            COMMIT;
            """, nativeQuery = true)
    void giamSPHD(@RequestParam(value = "idHoaDon") Integer idHD,
                  @RequestParam(value = "idCTSP") Integer idCTSP,
                  @RequestParam(value = "soLuong") Integer soLuong,
                  @RequestParam(value = "giaBan") Integer giaBan);

}
