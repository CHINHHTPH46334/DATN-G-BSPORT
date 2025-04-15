package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.response.ChiTietSanPhamView;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ChiTietSanPhamRepo extends JpaRepository<ChiTietSanPham, Integer> {
    @Query("SELECT c FROM ChiTietSanPham c WHERE c.sanPham.id_san_pham = :idSanPham")
    List<ChiTietSanPham> findBySanPhamIdSanPham(@Param("idSanPham") Integer idSanPham);

    @Query("SELECT c FROM ChiTietSanPham c WHERE LOWER(c.sanPham.ma_san_pham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.sanPham.ten_san_pham) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ChiTietSanPham> findBySanPham_MaSanPhamContainingIgnoreCaseOrSanPham_TenSanPhamContainingIgnoreCase(String keyword);

    Optional<ChiTietSanPham> findById(Integer id);

    @Query(nativeQuery = true, value = "select id_chi_tiet_san_pham, ma_san_pham, ten_san_pham, qr_code, gia_ban, so_luong, ctsp.trang_thai as trang_thai,\n" +
            "ctsp.ngay_tao, ctsp.ngay_sua, gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc, sp.id_san_pham, sp.id_danh_muc, sp.id_thuong_hieu, sp.id_chat_lieu\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu")
    ArrayList<ChiTietSanPhamView> listCTSP();

    @Query(nativeQuery = true, value = "select id_chi_tiet_san_pham,ma_san_pham, ten_san_pham, qr_code, gia_ban, so_luong, ctsp.trang_thai as trang_thai,\n" +
            "ctsp.ngay_tao, ctsp.ngay_sua,  gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu")
    Page<ChiTietSanPhamView> listPhanTrangChiTietSanPham(Pageable pageable);

    @Query(nativeQuery = true, value = "select id_chi_tiet_san_pham,ma_san_pham, ten_san_pham, qr_code, gia_ban, so_luong, ctsp.trang_thai as trang_thai,\n" +
            "ctsp.ngay_tao, ctsp.ngay_sua,  gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "        WHERE (:tenSanPham IS NULL OR ten_san_pham LIKE CONCAT('%', :tenSanPham, '%')) \n" +
            "        AND (:giaBanMin IS NULL OR gia_ban >= :giaBanMin) \n" +
            "        AND (:giaBanMax IS NULL OR gia_ban <= :giaBanMax) \n" +
            "        AND (:soLuongMin IS NULL OR so_luong >= :soLuongMin) \n" +
            "        AND (:soLuongMax IS NULL OR so_luong <= :soLuongMax) \n" +
            "        AND (:trangThai IS NULL OR ctsp.trang_thai like CONCAT('%', :trangThai, '%')) \n" +
            "        AND (:tenMauSac IS NULL OR ten_mau_sac like CONCAT('%', :tenMauSac, '%')) \n" +
            "        AND (:tenDanhMuc IS NULL OR ten_danh_muc like CONCAT('%', :tenDanhMuc, '%')) \n" +
            "        AND (:tenThuongHieu IS NULL OR ten_thuong_hieu like CONCAT('%', :tenThuongHieu, '%')) \n" +
            "        AND (:tenChatLieu IS NULL OR ten_chat_lieu like CONCAT('%', :tenChatLieu, '%'))")
    ArrayList<ChiTietSanPhamView> listLocCTSP(@Param("tenSanPham") String tenSanPham,
                                              @Param("giaBanMin") float giaBanMin,
                                              @Param("giaBanMax") float giaBanMax,
                                              @Param("soLuongMin") Integer soLuongMin,
                                              @Param("soLuongMax") Integer soLuongMax,
                                              @Param("trangThai") String trangThai,
                                              @Param("tenMauSac") String tenMauSac,
                                              @Param("tenDanhMuc") String tenDanhMuc,
                                              @Param("tenThuongHieu") String tenThuongHieu,
                                              @Param("tenChatLieu") String tenChatLieu);

    @Query(nativeQuery = true, value = "select id_chi_tiet_san_pham,ma_san_pham, ten_san_pham, qr_code, gia_ban, so_luong, ctsp.trang_thai as trang_thai,\n" +
            "ctsp.ngay_tao, ctsp.ngay_sua,  gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "where ctsp.id_san_pham = :idSanPham ")
    ArrayList<ChiTietSanPhamView> listCTSPFolowSanPham(@Param("idSanPham") Integer idSanPham);

    //=============================== Của Dũng====================================//
    @Query(value = """
        SELECT ctsp.id_chi_tiet_san_pham, sp.ten_san_pham, dm.ten_danh_muc, ms.ten_mau_sac AS ten_mau, kt.gia_tri,
                ctsp.so_luong, COALESCE(km_max.gia_sau_giam, ctsp.gia_ban) AS gia_sau_giam, ctsp.trang_thai, ctsp.gia_ban
        FROM chi_tiet_san_pham ctsp
        JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham
        JOIN danh_muc_san_pham dm ON sp.id_danh_muc = dm.id_danh_muc
        JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id_mau_sac
        JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id_kich_thuoc
        LEFT JOIN ( SELECT
                        ctkm.id_chi_tiet_san_pham,
                        MIN(ctkm.gia_sau_giam) AS gia_sau_giam
                    FROM chi_tiet_khuyen_mai ctkm
                    JOIN chi_tiet_san_pham ctsp2 ON ctsp2.id_chi_tiet_san_pham = ctkm.id_chi_tiet_san_pham
                    JOIN khuyen_mai km ON ctkm.id_khuyen_mai = km.id_khuyen_mai
                    WHERE km.trang_thai = N'Đang diễn ra'
                    AND GETDATE() BETWEEN km.ngay_bat_dau AND km.ngay_het_han
                    GROUP BY ctkm.id_chi_tiet_san_pham
                    ) km_max ON ctsp.id_chi_tiet_san_pham = km_max.id_chi_tiet_san_pham
        WHERE ctsp.trang_thai = N'Hoạt động'
        ORDER BY ctsp.id_chi_tiet_san_pham
        """, nativeQuery = true)
    Page<ChiTietSanPhamView> getAllCTSP_HD(Pageable pageable);

    @Query(value = """
        SELECT ctsp.id_chi_tiet_san_pham, sp.ten_san_pham, dm.ten_danh_muc, ms.ten_mau_sac AS ten_mau, kt.gia_tri,
                ctsp.so_luong, COALESCE(km_max.gia_sau_giam, ctsp.gia_ban) AS gia_sau_giam, ctsp.trang_thai, ctsp.gia_ban
        FROM chi_tiet_san_pham ctsp
        JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham
        JOIN danh_muc_san_pham dm ON sp.id_danh_muc = dm.id_danh_muc
        JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id_mau_sac
        JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id_kich_thuoc
        LEFT JOIN ( SELECT
                        ctkm.id_chi_tiet_san_pham,
                        MIN(ctkm.gia_sau_giam) AS gia_sau_giam
                    FROM chi_tiet_khuyen_mai ctkm
                    JOIN chi_tiet_san_pham ctsp2 ON ctsp2.id_chi_tiet_san_pham = ctkm.id_chi_tiet_san_pham
                    JOIN khuyen_mai km ON ctkm.id_khuyen_mai = km.id_khuyen_mai
                    WHERE km.trang_thai = N'Đang diễn ra'
                    AND GETDATE() BETWEEN km.ngay_bat_dau AND km.ngay_het_han
                    GROUP BY ctkm.id_chi_tiet_san_pham
                    ) km_max ON ctsp.id_chi_tiet_san_pham = km_max.id_chi_tiet_san_pham
        WHERE ctsp.trang_thai = N'Hoạt động'
        AND (sp.ten_san_pham LIKE CONCAT('%', :keyword, '%') OR dm.ten_danh_muc LIKE CONCAT('%', :keyword, '%'))
        ORDER BY ctsp.id_chi_tiet_san_pham
        """, nativeQuery = true)
    Page<ChiTietSanPhamView> searchCTSP_HD(@Param("keyword") String keyword, Pageable pageable);

    //=============================== Của Dũng====================================//
    @Query(nativeQuery = true, value = "WITH DanhGiaSanPham AS (\n" +
            "    SELECT\n" +
            "        id_chi_tiet_san_pham,\n" +
            "        AVG(ISNULL(danh_gia, 0) * 1.0) as danh_gia_trung_binh, -- Nhân 1.0 để đảm bảo kết quả là số thực\n" +
            "        COUNT(binh_luan) as so_luong_danh_gia -- Chỉ đếm các bình luận không rỗng\n" +
            "    FROM binh_luan\n" +
            "    GROUP BY id_chi_tiet_san_pham\n" +
            "),\n" +
            "-- CTE for calculating effective promotions\n" +
            "KhuyenMaiHieuLuc AS (\n" +
            "    SELECT\n" +
            "        ctkm.id_chi_tiet_san_pham,\n" +
            "        GiamGia = CASE\n" +
            "            WHEN km.kieu_giam_gia = N'Phần trăm' THEN ctsp.gia_ban * (1 - km.gia_tri_giam / 100.0)\n" +
            "            WHEN km.kieu_giam_gia = N'Tiền mặt' THEN ctsp.gia_ban - km.gia_tri_giam\n" +
            "            ELSE ctsp.gia_ban\n" +
            "        END,\n" +
            "        km.gia_tri_giam,\n" +
            "        km.kieu_giam_gia\n" +
            "    FROM chi_tiet_khuyen_mai ctkm\n" +
            "    JOIN khuyen_mai km\n" +
            "        ON ctkm.id_khuyen_mai = km.id_khuyen_mai\n" +
            "        AND GETDATE() BETWEEN km.ngay_bat_dau AND km.ngay_het_han\n" +
            "    JOIN chi_tiet_san_pham ctsp\n" +
            "        ON ctkm.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham\n" +
            "),\n" +
            "-- CTE for aggregating images per product detail\n" +
            "AnhSanPham AS (\n" +
            "    SELECT\n" +
            "        id_chi_tiet_san_pham,\n" +
            "        -- Nối tất cả hinh_anh thành một chuỗi, cách nhau bằng dấu phẩy\n" +
            "        -- WITHIN GROUP (ORDER BY ...) nếu bạn muốn ảnh có thứ tự cụ thể (ví dụ: theo id_hinh_anh)\n" +
            "        STRING_AGG(ISNULL(ha.hinh_anh, ''), ',') WITHIN GROUP (ORDER BY ha.id_hinh_anh) AS hinh_anh\n" +
            "        -- Nếu không cần thứ tự hoặc không có cột để sắp xếp:\n" +
            "        -- STRING_AGG(ISNULL(ha.hinh_anh, ''), ',') AS DanhSachHinhAnh\n" +
            "    FROM hinh_anh ha\n" +
            "    WHERE ha.hinh_anh IS NOT NULL AND ha.hinh_anh <> '' -- Chỉ nối các ảnh có giá trị\n" +
            "    GROUP BY id_chi_tiet_san_pham\n" +
            ")\n" +
            "-- Final Select statement combining all information\n" +
            "SELECT\n" +
            "    ctsp.id_chi_tiet_san_pham,\n" +
            "    sp.id_san_pham,\n" +
            "    sp.ma_san_pham,\n" +
            "    sp.ten_san_pham,\n" +
            "    sp.mo_ta,\n" +
            "    sp.trang_thai,\n" +
            "    dm.ten_danh_muc,\n" +
            "    th.ten_thuong_hieu,\n" +
            "    cl.ten_chat_lieu,\n" +
            "    ISNULL(asp.hinh_anh, '') AS hinh_anh, -- Lấy chuỗi ảnh đã nối từ CTE AnhSanPham\n" +
            "    kt.gia_tri , -- Đổi tên để rõ ràng hơn\n" +
            "    kt.don_vi,   -- Đổi tên để rõ ràng hơn\n" +
            "    ms.ma_mau_sac,\n" +
            "    ms.ten_mau_sac,\n" +
            "    kt.id_kich_thuoc,\n" +
            "    ms.id_mau_sac,\n" +
            "    ctsp.ngay_tao,\n" +
            "    ctsp.ngay_sua,\n" +
            "    ctsp.so_luong,\n" +
            "    ISNULL(dgs.danh_gia_trung_binh, 0) as danh_gia_trung_binh, -- Lấy từ CTE DanhGiaSanPham\n" +
            "    ISNULL(dgs.so_luong_danh_gia, 0) as so_luong_danh_gia,    -- Lấy từ CTE DanhGiaSanPham\n" +
            "    ctsp.gia_ban AS GiaGoc,\n" +
            "    ISNULL(kh.GiamGia, ctsp.gia_ban) AS GiaHienTai,\n" +
            "    kh.gia_tri_giam AS GiaTriKhuyenMai,\n" +
            "    kh.kieu_giam_gia AS KieuKhuyenMai\n" +
            "FROM chi_tiet_san_pham ctsp\n" +
            "INNER JOIN san_pham sp ON sp.id_san_pham = ctsp.id_san_pham\n" +
            "INNER JOIN danh_muc_san_pham dm ON sp.id_danh_muc = dm.id_danh_muc\n" +
            "INNER JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id_thuong_hieu\n" +
            "INNER JOIN chat_lieu cl ON sp.id_chat_lieu = cl.id_chat_lieu\n" +
            "LEFT JOIN KhuyenMaiHieuLuc kh ON ctsp.id_chi_tiet_san_pham = kh.id_chi_tiet_san_pham\n" +
            "LEFT JOIN DanhGiaSanPham dgs ON ctsp.id_chi_tiet_san_pham = dgs.id_chi_tiet_san_pham\n" +
            "LEFT JOIN kich_thuoc kt ON kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "LEFT JOIN mau_sac ms ON ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "LEFT JOIN AnhSanPham asp ON ctsp.id_chi_tiet_san_pham = asp.id_chi_tiet_san_pham -- Join với CTE ảnh đã nối\n" +
            "WHERE\n" +
            "    sp.trang_thai = N'Hoạt động'\n" +
            "    AND sp.id_san_pham = :idSanPham; -- Lọc theo sản phẩm cụ thể\n")
    ArrayList<ChiTietSanPhamView> getCTSPBySanPhamFull(@Param("idSanPham") Integer idSanPham);

    //ddd
    @Modifying
    @Transactional
    @Query(value = """
            update chi_tiet_san_pham
            set
                so_luong = so_luong + :soLuong
                where id_chi_tiet_san_pham = :idCTSP
            """, nativeQuery = true)
    void updateSLCTSPByIdCTSP(@RequestParam("idCTSP") Integer idCTSP,
                              @RequestParam("soLuong") Integer soLuong);

    @Query(value = """
            SELECT\s
                ctsp.id_chi_tiet_san_pham,
                ma_san_pham,
                ten_san_pham,
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
                END AS gia_ban,
                so_luong,
                ctsp.trang_thai AS trang_thai,
                gia_tri,
                ten_mau_sac AS ten_mau,
                ten_danh_muc,
                ten_thuong_hieu,
                ten_chat_lieu,
                hinh_anh
            FROM chi_tiet_san_pham ctsp
            FULL OUTER JOIN san_pham sp ON sp.id_san_pham = ctsp.id_san_pham
            FULL OUTER JOIN kich_thuoc kt ON kt.id_kich_thuoc = ctsp.id_kich_thuoc
            FULL OUTER JOIN mau_sac ms ON ms.id_mau_sac = ctsp.id_mau_sac
            FULL OUTER JOIN danh_muc_san_pham dm ON dm.id_danh_muc = sp.id_danh_muc
            FULL OUTER JOIN thuong_hieu th ON th.id_thuong_hieu = sp.id_thuong_hieu
            FULL OUTER JOIN chat_lieu cl ON cl.id_chat_lieu = sp.id_chat_lieu
            FULL OUTER JOIN chi_tiet_khuyen_mai ctkm ON ctkm.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
            FULL OUTER JOIN khuyen_mai km ON km.id_khuyen_mai = ctkm.id_khuyen_mai
            WHERE ctsp.trang_thai like N'Hoạt động'
            """, nativeQuery = true)
    List<ChiTietSanPhamView> getAllCTSPKM();
    //////
}
