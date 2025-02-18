package com.example.gbsports.repository;

import com.example.gbsports.entity.SanPham;
import com.example.gbsports.response.SanPhamView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface SanPhamRepo extends JpaRepository<SanPham, Integer> {
    @Query(nativeQuery = true, value = "select id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, gioi_tinh, dm.ten_danh_muc as ten_danh_muc, \n" +
            "th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu\n" +
            "from san_pham sp\n" +
            "join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu")
    ArrayList<SanPhamView> getAllSanPham();
    @Query(nativeQuery = true, value = "select id_san_pham, ma_san_pham, ten_san_pham, mo_ta, sp.trang_thai as trang_thai, gioi_tinh, dm.ten_danh_muc as ten_danh_muc, \n" +
            "th.ten_thuong_hieu as ten_thuong_hieu, ten_chat_lieu\n" +
            "from san_pham sp\n" +
            "join danh_muc_san_pham dm on dm.id_danh_muc = sp.id_danh_muc\n" +
            "join thuong_hieu th on th.id_thuong_hieu = sp.id_thuong_hieu\n" +
            "join chat_lieu cl on cl.id_chat_lieu = sp.id_chat_lieu")
    Page<SanPhamView> getAllSanPhamPhanTrang(Pageable pageable);

}
