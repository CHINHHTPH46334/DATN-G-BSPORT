package com.example.gbsports.repository;

import com.example.gbsports.entity.SanPham;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.response.SanPhamView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface SanPhamRepo extends JpaRepository<SanPham, Integer> {
    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n"
            +
            "            th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong\n"
            +
            "            from san_pham sp\n" +
            "          full outer  join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "           full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "          full outer  join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "\t\t\t full outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "\t\t\tgroup by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, dm.ten_danh_muc, \n"
            +
            "            th.ten_thuong_hieu, ten_chat_lieu,hinh_anh")
    ArrayList<SanPhamView> getAllSanPham();

    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n"
            +
            "th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong, max(ctsp.ngay_sua) as ngay_sua_moi\n"
            +
            "from san_pham sp\n" +
            "full outer  join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer  join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "full outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "group by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, dm.ten_danh_muc,\n" +
            "th.ten_thuong_hieu, ten_chat_lieu,hinh_anh\n" +
            "order by ngay_sua_moi desc\n")
    ArrayList<SanPhamView> getAllSanPhamSapXepTheoNgaySua();

    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n"
            +
            "            th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong\n"
            +
            "            from san_pham sp\n" +
            "           full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "           full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "            full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "\t\t\tfull outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "\t\t\tgroup by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, dm.ten_danh_muc, \n"
            +
            "            th.ten_thuong_hieu, ten_chat_lieu,hinh_anh")
    Page<SanPhamView> getAllSanPhamPhanTrang(Pageable pageable);

    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n"
            +
            "            th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong\n"
            +
            "            from san_pham sp\n" +
            "          full outer  join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "           full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "           full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "\t\t\tfull outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "\t\t\tgroup by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, dm.ten_danh_muc, \n"
            +
            "            th.ten_thuong_hieu, ten_chat_lieu,hinh_anh" +
            "where ten_danh_muc like CONCAT('%', :tenDanhMuc, '%') and ten_thuong_hieu like CONCAT('%', :tenThuongHieu, '%') and ten_chat_lieu like CONCAT('%', :tenChatLieu, '%')")
    ArrayList<SanPhamView> locSanPham(@Param("tenDanhMuc") String tenDanhMuc,
                                      @Param("tenThuongHieu") String tenThuongHieu, @Param("tenChatLieu") String tenChatLieu);

    // Tìm kiếm sản phẩm theo mã hoặc tên với phân trang
    @Query("SELECT sp FROM SanPham sp WHERE LOWER(sp.ma_san_pham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.ten_san_pham) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SanPham> findByMaSanPhamOrTenSanPhamContainingIgnoreCase(String keyword, Pageable pageable);

    // Lấy tất cả sản phẩm, sắp xếp theo ID với phân trang
    @Query("SELECT sp FROM SanPham sp ORDER BY sp.id_san_pham")
    Page<SanPham> findAllSortedByIdSanPham(Pageable pageable);

    @Query(nativeQuery = true, value = "WITH KhuyenMaiHieuLuc AS (\n" +
            "\t\t\t\t\t\tSELECT \n" +
            "\t\t\t\t\t\t\tctkm.id_chi_tiet_san_pham,\n" +
            "\t\t\t\t\t\t\tGiamGia = CASE \n" +
            "\t\t\t\t\t\t\t\tWHEN km.kieu_giam_gia = N'Phần trăm' THEN ctsp.gia_ban * (1 - km.gia_tri_giam / 100)\n"
            +
            "\t\t\t\t\t\t\t\tWHEN km.kieu_giam_gia = N'Tiền mặt' THEN ctsp.gia_ban - km.gia_tri_giam\n" +
            "\t\t\t\t\t\t\t\tELSE ctsp.gia_ban\n" +
            "\t\t\t\t\t\t\tEND\n" +
            "\t\t\t\t\t\tFROM chi_tiet_khuyen_mai ctkm\n" +
            "\t\t\t\t\t\tJOIN khuyen_mai km \n" +
            "\t\t\t\t\t\t\tON ctkm.id_khuyen_mai = km.id_khuyen_mai\n" +
            "\t\t\t\t\t\t\tAND GETDATE() BETWEEN km.ngay_bat_dau AND km.ngay_het_han\n" +
            "\t\t\t\t\t\tJOIN chi_tiet_san_pham ctsp \n" +
            "\t\t\t\t\t\t\tON ctkm.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham\n" +
            "\t\t\t\t\t),\n" +
            "\t\t\t\t\tGiaTotNhat AS (\n" +
            "\t\t\t\t\t\tSELECT\n" +
            "\t\t\t\t\t\t\tctsp.id_san_pham,\n" +
            "\t\t\t\t\t\t\tGiaGiamMin = MIN(ISNULL(kh.GiamGia, ctsp.gia_ban)), -- Xử lý NULL\n" +
            "\t\t\t\t\t\t\tGiaGiamMax = MAX(ISNULL(kh.GiamGia, ctsp.gia_ban))\n" +
            "\t\t\t\t\t\tFROM chi_tiet_san_pham ctsp\n" +
            "\t\t\t\t\t\tLEFT JOIN KhuyenMaiHieuLuc kh \n" +
            "\t\t\t\t\t\t\tON ctsp.id_chi_tiet_san_pham = kh.id_chi_tiet_san_pham\n" +
            "\t\t\t\t\t\tGROUP BY ctsp.id_san_pham\n" +
            "\t\t\t\t\t)\n" +
            "\n" +
            "\t\t\t\t\tSELECT DISTINCT\n" +
            "\t\t\t\t\t\tsp.id_san_pham,\n" +
            "\t\t\t\t\t\tsp.ma_san_pham,\n" +
            "\t\t\t\t\t\tsp.ten_san_pham,\n" +
            "\t\t\t\t\t\tsp.mo_ta,\n" +
            "\t\t\t\t\t\tsp.trang_thai,\n" +
            "\t\t\t\t\t\tdm.ten_danh_muc,\n" +
            "\t\t\t\t\t\tth.ten_thuong_hieu,\n" +
            "\t\t\t\t\t\tcl.ten_chat_lieu,\n" +
            "\t\t\t\t\t\tsp.hinh_anh,\n" +
            "\t\t\t\t\t\tavg(bl.danh_gia) over (PARTITION BY sp.id_san_pham) as danh_gia,\n" +
            "\t\t\t\t\t\tcount(bl.danh_gia) over(PARTITION BY sp.id_san_pham) as so_luong_danh_gia,\n" +
            "\t\t\t\t\t\tSUM(ctsp.so_luong) OVER (PARTITION BY sp.id_san_pham) AS tong_so_luong,\n" +
            "\t\t\t\t\t\tMAX(ctsp.gia_ban) OVER (PARTITION BY sp.id_san_pham) AS gia_max,\n" +
            "\t\t\t\t\t\tMIN(ctsp.gia_ban) OVER (PARTITION BY sp.id_san_pham) AS gia_min,\n" +
            "\t\t\t\t\t\t-- Đảm bảo không bị NULL\n" +
            "\t\t\t\t\t\tCOALESCE(gt.GiaGiamMin, MIN(ctsp.gia_ban) OVER (PARTITION BY sp.id_san_pham)) AS gia_tot_nhat,\n"
            +
            "\t\t\t\t\t\tCOALESCE(gt.GiaGiamMax, MAX(ctsp.gia_ban) OVER (PARTITION BY sp.id_san_pham)) AS gia_khuyen_mai_cao_nhat\n"
            +
            "\t\t\t\t\tFROM san_pham sp\n" +
            "\t\t\t\t\tINNER JOIN danh_muc_san_pham dm \n" +
            "\t\t\t\t\t\tON sp.id_danh_muc = dm.id_danh_muc\n" +
            "\t\t\t\t\tINNER JOIN thuong_hieu th \n" +
            "\t\t\t\t\t\tON sp.id_thuong_hieu = th.id_thuong_hieu\n" +
            "\t\t\t\t\tINNER JOIN chat_lieu cl \n" +
            "\t\t\t\t\t\tON sp.id_chat_lieu = cl.id_chat_lieu\n" +
            "\t\t\t\t\tINNER JOIN chi_tiet_san_pham ctsp \n" +
            "\t\t\t\t\t\tON sp.id_san_pham = ctsp.id_san_pham\n" +
            "\t\t\t\t\tLEFT JOIN GiaTotNhat gt \n" +
            "\t\t\t\t\t\tON sp.id_san_pham = gt.id_san_pham\n" +
            "\t\t\t\t\tleft join binh_luan bl \n" +
            "\t\t\t\t\t\ton bl.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham\n" +
            "\t\t\t\t\tWHERE \n" +
            "\t\t\t\t\t\tsp.trang_thai = N'Hoạt động'\n" +
            "    AND sp.ten_san_pham LIKE CONCAT('%', :tenSanPham ,'%');")
    ArrayList<SanPhamView> listSanPhamBanHangWebTheoSP(@Param("tenSanPham") String tenSanPham);

    @Query("SELECT s FROM SanPham s WHERE LOWER(s.ma_san_pham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.ten_san_pham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY s.id_san_pham DESC")
    List<SanPham> findByMaSanPhamOrTenSanPhamContainingIgnoreCase(@Param("keyword") String keyword);

    @Query("SELECT s FROM SanPham s ORDER BY s.id_san_pham DESC")
    List<SanPham> findAllSortedByIdSanPham();



}
