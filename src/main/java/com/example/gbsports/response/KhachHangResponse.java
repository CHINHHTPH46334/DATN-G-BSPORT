package com.example.gbsports.response;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface KhachHangResponse {
    Integer getIdKhachHang();
    String getMaKhachHang();
    String getTenKhachHang();

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date getNgaySinh();

    String getEmail();
    Boolean getGioiTinh();
    String getSoDienThoai();
    String getTrangThai();
}
