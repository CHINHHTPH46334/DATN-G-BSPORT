package com.example.gbsports.response;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface TheoDoiDonHangResponse {
    Integer getId_don_hang();
    Integer getId_hoa_don();
    String getTrang_thai();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_chuyen();
    String getNhan_vien_doi();
    String getNoi_dung_doi();

    // Thêm một phương thức để lấy chuỗi định dạng
    default String getNgay_chuyen_formatted() {
        if (getNgay_chuyen() == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        return getNgay_chuyen().format(formatter);
    }
}
