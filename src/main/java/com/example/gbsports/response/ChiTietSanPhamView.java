package com.example.gbsports.response;

import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.SanPham;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface ChiTietSanPhamView {
     Integer getId_chi_tiet_san_pham();
     String getMa_san_pham();
     String getTen_san_pham();
     String getQr_code();
     float getGia_ban();
     Integer getSo_luong();
     String getTrang_thai();
     Date getNgay_tao();
     Date getNgay_sua();
     float getGia_nhap();
     String getGia_tri();
     String getDon_vi();
     String getTen_mau();
     String getTen_danh_muc();
     String getTen_thuong_hieu();
     String getTen_chat_lieu();
     Boolean getGioi_tinh();
     Integer getId_kich_thuoc();
     Integer getId_mau_sac();
}
