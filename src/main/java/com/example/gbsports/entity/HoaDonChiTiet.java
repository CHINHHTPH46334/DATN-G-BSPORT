package com.example.gbsports.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_hoa_don_chi_tiet;
    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    @JsonIgnore
    private HoaDon hoaDon;
    @ManyToOne
    @JoinColumn(name = "id_chi_tiet_san_pham")
    @JsonIgnore
    private ChiTietSanPham chiTietSanPham;
    private Integer so_luong;
    private BigDecimal don_gia;
}
