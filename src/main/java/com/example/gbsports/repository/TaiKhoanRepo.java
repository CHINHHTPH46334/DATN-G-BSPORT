package com.example.gbsports.repository;

import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepo extends JpaRepository<TaiKhoan, Integer> {
    @Query("SELECT t FROM TaiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap")
    Optional<TaiKhoan> findByTen_dang_nhap(String tenDangNhap);

    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.ten_dang_nhap = :tenDangNhap")
    List<TaiKhoan> findAllByTenDangNhap(String tenDangNhap);

    // Tìm tài khoản nhân viên (id_roles = 1, 2, 3)
    @Query("SELECT t FROM TaiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap AND t.roles.id_roles IN (1, 2, 3)")
    Optional<TaiKhoan> findByTenDangNhapAndNhanVienRoles(@Param("tenDangNhap") String tenDangNhap);

    // Tìm tài khoản khách hàng (id_roles = 4)
    @Query("SELECT t FROM TaiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap AND t.roles.id_roles = 4")
    Optional<TaiKhoan> findByTenDangNhapAndKhachHangRole(@Param("tenDangNhap") String tenDangNhap);

    // Lấy thông tin chi tiết nhân viên
    @Query("SELECT nv FROM NhanVien nv JOIN nv.taiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap AND t.roles.id_roles IN (1, 2, 3)")
    Optional<NhanVien> findNhanVienByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

    // Lấy thông tin chi tiết khách hàng
    @Query("SELECT kh FROM KhachHang kh JOIN kh.taiKhoan t WHERE t.ten_dang_nhap = :tenDangNhap AND t.roles.id_roles = 4")
    Optional<KhachHang> findKhachHangByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

}