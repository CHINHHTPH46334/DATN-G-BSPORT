package com.example.gbsports.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public interface NhanVienResponse {
    Integer getIdNhanVien();
    String getMaNhanVien();
    String getTenNhanVien();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone = "Asia/Ho_Chi_Minh")
    Date getNgaySinh();
    String getEmail();
    String getDiaChiLienHe();
    boolean getGioiTinh();
    String getSoDienThoai();
    String getTrangThai();
    String getAnhNhanVien();
    Date getNgayThamGia();
}
