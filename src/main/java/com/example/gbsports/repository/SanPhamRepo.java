package com.example.gbsports.repository;

import com.example.gbsports.entity.SanPham;
import com.example.gbsports.response.SanPhamView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface SanPhamRepo extends JpaRepository<SanPham, Integer> {
    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n" +
            "            th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong\n" +
            "            from san_pham sp\n" +
            "          full outer  join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "           full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "          full outer  join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "\t\t\t full outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "\t\t\tgroup by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, gioi_tinh, dm.ten_danh_muc, \n" +
            "            th.ten_thuong_hieu, ten_chat_lieu,hinh_anh")
    ArrayList<SanPhamView> getAllSanPham();

    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n" +
            "th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong, max(ctsp.ngay_sua) as ngay_sua_moi\n" +
            "from san_pham sp\n" +
            "full outer  join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "full outer  join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "full outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "group by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, gioi_tinh, dm.ten_danh_muc,\n" +
            "th.ten_thuong_hieu, ten_chat_lieu,hinh_anh\n" +
            "order by ngay_sua_moi desc\n"
    )
    ArrayList<SanPhamView> getAllSanPhamSapXepTheoNgaySua();

    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n" +
            "            th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong\n" +
            "            from san_pham sp\n" +
            "           full outer join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "           full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "            full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "\t\t\tfull outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "\t\t\tgroup by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, gioi_tinh, dm.ten_danh_muc, \n" +
            "            th.ten_thuong_hieu, ten_chat_lieu,hinh_anh")
    Page<SanPhamView> getAllSanPhamPhanTrang(Pageable pageable);

    @Query(nativeQuery = true, value = "select sp.id_san_pham as id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, dm.ten_danh_muc as ten_danh_muc, \n" +
            "            th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu, hinh_anh, sum(ctsp.so_luong) as tong_so_luong\n" +
            "            from san_pham sp\n" +
            "          full outer  join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "           full outer join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "           full outer join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu\n" +
            "\t\t\tfull outer join chi_tiet_san_pham ctsp on ctsp.id_san_pham = sp.id_san_pham\n" +
            "\t\t\tgroup by sp.id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai, gioi_tinh, dm.ten_danh_muc, \n" +
            "            th.ten_thuong_hieu, ten_chat_lieu,hinh_anh" +
            "where ten_danh_muc like CONCAT('%', :tenDanhMuc, '%') and ten_thuong_hieu like CONCAT('%', :tenThuongHieu, '%') and ten_chat_lieu like CONCAT('%', :tenChatLieu, '%')")
    ArrayList<SanPhamView> locSanPham(@Param("tenDanhMuc") String tenDanhMuc, @Param("tenThuongHieu") String tenThuongHieu, @Param("tenChatLieu") String tenChatLieu);

}
