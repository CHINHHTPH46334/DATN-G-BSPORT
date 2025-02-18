package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "hinh_anh")
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_hinh_anh;
    @ManyToOne
    @JoinColumn(name = "id_chi_tiet_san_pham")
    ChiTietSanPham chiTietSanPham;
    private Boolean anh_chinh;
    private String hinh_anh;
}
