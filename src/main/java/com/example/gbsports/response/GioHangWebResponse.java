package com.example.gbsports.response;

import java.math.BigDecimal;
public interface GioHangWebResponse {
    Integer getId_gio_hang();
    Integer getId_chi_tiet_san_pham();
    Integer getId_khach_hang();
    String getHinh_anh();
    String getTen_san_pham();
    String getTen_mau_sac();
    String getGia_tri();
    String getDon_vi();
    Integer getSo_luong();
    BigDecimal getGia_ban();
    // địa chỉ KH
    String getSo_nha();
    String getXa_phuong();
    String getQuan_huyen();
    String getTinh_thanh_pho();
    String getDia_chi_mac_dinh();
}
