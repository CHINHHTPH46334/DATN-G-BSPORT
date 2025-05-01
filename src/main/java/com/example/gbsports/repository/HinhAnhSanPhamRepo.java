package com.example.gbsports.repository;

import com.example.gbsports.entity.HinhAnhSanPham;
import com.example.gbsports.response.HinhAnhView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface HinhAnhSanPhamRepo extends JpaRepository<HinhAnhSanPham, Integer> {
    @Query(nativeQuery = true, value = "SELECT ha.*\n" +
            "FROM hinh_anh ha\n" +
            "JOIN chi_tiet_san_pham ctsp ON ctsp.id_chi_tiet_san_pham = ha.id_chi_tiet_san_pham\n" +
            "WHERE ha.id_chi_tiet_san_pham = :idChiTietSanPham")
    List<HinhAnhView> listHinhAnhTheoSanPham(@Param("idChiTietSanPham") Integer idChiTietSanPham);

}
