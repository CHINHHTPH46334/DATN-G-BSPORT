package com.example.gbsports.response;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface NhanVienResponse {
     Integer getId_nhan_vien();
     String getMa_nhan_vien();
     String getTen_nhan_vien();
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
     Date getNgay_sinh();
     String getEmail();
     String getDia_chi_lien_he();
     boolean getGioi_tinh();
     String getSo_dien_thoai();
     String getTrang_thai();
}
