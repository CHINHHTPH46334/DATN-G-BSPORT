package com.example.gbsports.repository;

import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.response.NhanVienResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NhanVienRepo extends JpaRepository<NhanVien, Integer> {
    @Query(nativeQuery = true, value = "select id_nhan_vien, ma_nhan_vien,ten_nhan_vien, ngay_sinh, email, dia_chi_lien_he, gioi_tinh, so_dien_thoai, trang_thai from nhan_vien")
    List<NhanVienResponse> getAll();

    @Query(nativeQuery = true, value = "select id_nhan_vien, ma_nhan_vien,ten_nhan_vien, ngay_sinh, email, dia_chi_lien_he, gioi_tinh, so_dien_thoai, trang_thai from nhan_vien")
    Page<NhanVienResponse> listPT(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM nhan_vien WHERE \n" +
            "(email like %:keyword% OR \n" +
            "LOWER(ten_nhan_vien) LIKE LOWER(CONCAT('%', :keyword, '%')) OR \n" +
            "so_dien_thoai like %:keyword%)")
    List<NhanVienResponse> timNhanVien(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM nhan_vien WHERE (trang_thai = :trangThai)", nativeQuery = true)
    List<NhanVienResponse> locNhanVienTheoTrangThai(@Param("trangThai") String trangThai);


}
