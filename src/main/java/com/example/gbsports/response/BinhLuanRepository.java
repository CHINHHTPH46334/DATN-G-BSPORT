package com.example.gbsports.response;

import com.example.gbsports.entity.BinhLuan;
import com.example.gbsports.entity.BinhLuanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, BinhLuanId> {

    @Query("SELECT b FROM BinhLuan b WHERE b.id_chi_tiet_san_pham = :idChiTietSanPham ORDER BY b.ngay_binh_luan DESC")
    List<BinhLuan> findByIdChiTietSanPham(Integer idChiTietSanPham);

    @Query("SELECT COUNT(b) FROM BinhLuan b WHERE b.id_chi_tiet_san_pham = :idChiTietSanPham")
    long countByIdChiTietSanPham(Integer idChiTietSanPham);

    @Query("SELECT AVG(b.danh_gia) FROM BinhLuan b WHERE b.id_chi_tiet_san_pham = :idChiTietSanPham")
    Double getAverageRatingByIdChiTietSanPham(Integer idChiTietSanPham);

    @Query("SELECT b FROM BinhLuan b WHERE b.id_khach_hang = :idKhachHang AND b.id_chi_tiet_san_pham = :idChiTietSanPham")
    Optional<BinhLuan> findByIdKhachHangAndIdChiTietSanPham(Integer idKhachHang, Integer idChiTietSanPham);

    @Modifying
    @Transactional
    @Query("DELETE FROM BinhLuan b WHERE b.id_khach_hang = :idKhachHang AND b.id_chi_tiet_san_pham = :idChiTietSanPham")
    void deleteById_KhachHangAndId_ChiTietSanPham(Integer idKhachHang, Integer idChiTietSanPham);
}
