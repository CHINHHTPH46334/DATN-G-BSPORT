package com.example.gbsports.repository;

import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.response.KhachHangResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepo extends JpaRepository<KhachHang, Integer> {

    @Query(nativeQuery = true, value = "SELECT id_khach_hang, ma_khach_hang, ten_khach_hang, ngay_sinh, email, gioi_tinh, so_dien_thoai, trang_thai FROM khach_hang")
    List<KhachHangResponse> getAll();

    @Query(nativeQuery = true, value = "SELECT id_khach_hang, ma_khach_hang, ten_khach_hang, ngay_sinh, email, gioi_tinh, so_dien_thoai, trang_thai FROM khach_hang")
    Page<KhachHangResponse> listPT(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM khach_hang WHERE " +
            "(email LIKE %:keyword% OR " +
            "LOWER(ten_khach_hang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "so_dien_thoai LIKE %:keyword% OR " +
            "LOWER(ma_khach_hang) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<KhachHangResponse> timKhachHang(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM khach_hang WHERE trang_thai = :trangThai", nativeQuery = true)
    List<KhachHangResponse> locKhachHangTheoTrangThai(@Param("trangThai") String trangThai);

    @Query("SELECT k FROM KhachHang k WHERE k.idKhachHang = :id")
    Optional<KhachHang> findOriginalById(@Param("id") Long id);
}
