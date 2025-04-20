package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietKhuyenMaiRepo extends JpaRepository<ChiTietKhuyenMai,Integer> {
    @Query("SELECT c FROM ChiTietKhuyenMai c JOIN FETCH c.chiTietSanPham WHERE c.khuyenMai.id = :khuyenMaiId")
    List<ChiTietKhuyenMai> findByKhuyenMaiId(Integer khuyenMaiId);
    void deleteByKhuyenMaiId(Integer khuyenMaiId);

    @Query("SELECT ckm FROM ChiTietKhuyenMai ckm WHERE ckm.chiTietSanPham.id_chi_tiet_san_pham = :idChiTietSanPham")
    List<ChiTietKhuyenMai> findAllByChiTietSanPhamId(@Param("idChiTietSanPham") Integer idChiTietSanPham);

}
