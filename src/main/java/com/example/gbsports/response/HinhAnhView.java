package com.example.gbsports.response;

import com.example.gbsports.entity.ChiTietSanPham;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public interface HinhAnhView {
    Integer getId_hinh_anh();
    Integer getId_chi_tiet_san_pham();
    Boolean getAnh_chinh();
    String getHinh_anh();
}
