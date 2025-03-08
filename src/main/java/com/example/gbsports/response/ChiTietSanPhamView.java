package com.example.gbsports.response;

import java.util.Date;

public interface ChiTietSanPhamView {
     Integer getId_chi_tiet_san_pham();
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
}
