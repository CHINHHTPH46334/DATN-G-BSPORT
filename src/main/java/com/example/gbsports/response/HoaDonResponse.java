package com.example.gbsports.response;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface HoaDonResponse {
    Integer getId_hoa_don();
    String getMa_hoa_don();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_tao();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_sua();
    String getTrang_thai();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_chuyen();
    String getSdt_nguoi_nhan();
    String getDia_chi();
    String getMa_voucher();
    String getEmail();
    BigDecimal getTong_tien_truoc_giam();
    BigDecimal getPhi_van_chuyen();
    String getHo_ten();
    BigDecimal getTong_tien_sau_giam();
    String getHinh_thuc_thanh_toan();
    String getPhuong_thuc_nhan_hang();
    String getTen_nhan_vien();
}
