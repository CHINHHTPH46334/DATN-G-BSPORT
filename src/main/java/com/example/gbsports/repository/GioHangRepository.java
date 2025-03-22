package com.example.gbsports.repository;

import com.example.gbsports.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    @Query("SELECT g FROM GioHang g WHERE g.khachHang.idKhachHang = :idKhachHang")
    Optional<GioHang> findByKhachHangId(@Param("idKhachHang") Integer idKhachHang);

    @Query("SELECT g FROM GioHang g JOIN FETCH g.chiTietGioHangs WHERE g.khachHang.idKhachHang = :idKhachHang")
    Optional<GioHang> findByKhachHangIdWithDetails(@Param("idKhachHang") Integer idKhachHang);

}
