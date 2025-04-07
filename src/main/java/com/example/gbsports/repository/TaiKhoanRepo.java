package com.example.gbsports.repository;

import com.example.gbsports.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepo extends JpaRepository<TaiKhoan, Integer> {
    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.ten_dang_nhap = :tenDangNhap")
    List<TaiKhoan> findAllByTenDangNhap(String tenDangNhap);

    // Tìm tài khoản theo ten_dang_nhap và nhóm id_roles (1, 2, 3 - Nhân viên)
    @Query("SELECT t FROM TaiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap AND t.roles.id_roles IN (1, 2, 3)")
    Optional<TaiKhoan> findByTenDangNhapAndNhanVienRoles(@Param("tenDangNhap") String tenDangNhap);

    // Tìm tài khoản theo ten_dang_nhap và id_roles = 4 (khách hàng)
    @Query("SELECT t FROM TaiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap AND t.roles.id_roles = 4")
    Optional<TaiKhoan> findByTenDangNhapAndKhachHangRole(@Param("tenDangNhap") String tenDangNhap);
}