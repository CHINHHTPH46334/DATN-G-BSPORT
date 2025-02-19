package com.example.gbsports.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_hoa_don_chi_tiet;
//    id_hoa_don int references hoa_don(id_hoa_don),
//    id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
    private Integer so_luong;
    private Double don_gia;
}
