package com.example.gbsports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ChiTietGioHangId implements Serializable {
    @Column(name = "id_gio_hang")
    private Integer idGioHang;

    @Column(name = "id_chi_tiet_san_pham")
    private Integer idChiTietSanPham;
}
