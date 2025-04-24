package com.example.gbsports.response;

import java.math.BigDecimal;

public interface ChiTietTraHangResponse {
    Integer getId_ct_tra_hang();
    Integer getId_chi_tiet_san_pham();
    Integer getSo_luong();
    BigDecimal getThanh_tien(); // Tiền hoàn
    BigDecimal getDon_gia(); // Đơn giá
    String getTen_san_pham();
    String getHinh_anh();
    String getGia_tri(); // Kích thước
    String getTen_mau_sac();
}
