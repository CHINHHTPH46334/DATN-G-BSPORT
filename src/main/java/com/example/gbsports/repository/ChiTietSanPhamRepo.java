package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.response.ChiTietSanPhamView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            "ctsp.ngay_tao, ctsp.ngay_sua, gia_nhap, gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu")
    ArrayList<ChiTietSanPhamView> listCTSP();

    @Query(nativeQuery = true, value = "select id_chi_tiet_san_pham,ma_san_pham, ten_san_pham, qr_code, gia_ban, so_luong, ctsp.trang_thai as trang_thai,\n" +
            "ctsp.ngay_tao, ctsp.ngay_sua, gia_nhap, gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu")
    Page<ChiTietSanPhamView> listPhanTrangChiTietSanPham(Pageable pageable);

    @Query(nativeQuery = true, value = "select id_chi_tiet_san_pham,ma_san_pham, ten_san_pham, qr_code, gia_ban, so_luong, ctsp.trang_thai as trang_thai,\n" +
            "ctsp.ngay_tao, ctsp.ngay_sua, gia_nhap, gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
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
            "ctsp.ngay_tao, ctsp.ngay_sua, gia_nhap, gia_tri, don_vi, ten_mau_sac as ten_mau, ten_danh_muc, ten_thuong_hieu, ten_chat_lieu, ctsp.id_mau_sac,ctsp.id_kich_thuoc\n" +
            "from chi_tiet_san_pham ctsp\n" +
            "full outer join san_pham sp on sp.id_san_pham = ctsp.id_san_pham\n" +
            "full outer join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc\n" +
            "full outer join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac\n" +
            "full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "where ctsp.id_san_pham = :idSanPham ")
    ArrayList<ChiTietSanPhamView> listCTSPFolowSanPham(@Param("idSanPham") Integer idSanPham);

}
