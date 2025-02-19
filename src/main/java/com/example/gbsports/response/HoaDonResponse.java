package com.example.gbsports.response;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface HoaDonResponse {
    Integer getId_hoa_don();
    String getMa_hoa_don();
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date getNgay_tao();
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date getNgay_sua();
    String getTrang_thai();
    String getSdt_nguoi_nhan();
    String getDia_chi();
    String getEmail();
    float getTong_tien_truoc_giam();
    float getPhi_van_chuyen();
    String getHo_ten();
    float getTong_tien_sau_giam();
    String getHinh_thuc_thanh_toan();
    String getPhuong_thuc_nhan_hang();
}
