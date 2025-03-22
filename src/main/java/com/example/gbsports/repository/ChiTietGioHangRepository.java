package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietGioHang;
import com.example.gbsports.entity.ChiTietGioHangId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Integer> {

    Optional<ChiTietGioHang> findById(ChiTietGioHangId chiTietGioHangId);
}
