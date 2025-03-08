package com.example.gbsports.repository;

import com.example.gbsports.entity.DiaChiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaChiKhachHangRepo extends JpaRepository<DiaChiKhachHang, Integer> {
    @Query("SELECT d FROM DiaChiKhachHang d WHERE d.khachHang.idKhachHang = :idKhachHang")
    List<DiaChiKhachHang> findByKhachHangId(@Param("idKhachHang") Integer idKhachHang);
}