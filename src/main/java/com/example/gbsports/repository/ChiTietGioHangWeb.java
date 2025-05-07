package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietGioHang;
import com.example.gbsports.entity.ChiTietGioHangId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietGioHangWeb extends JpaRepository<ChiTietGioHang, ChiTietGioHangId> {
}
