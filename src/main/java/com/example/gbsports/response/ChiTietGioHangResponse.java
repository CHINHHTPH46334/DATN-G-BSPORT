package com.example.gbsports.response;

import java.math.BigDecimal;

public interface ChiTietGioHangResponse{
    Integer getId_gio_hang();
    String getMa_san_pham();
    String getTen_san_pham();
    String getHinh_anh();
    BigDecimal getDon_gia();
    BigDecimal getGia_ban();
    Integer getSo_luong();
}
