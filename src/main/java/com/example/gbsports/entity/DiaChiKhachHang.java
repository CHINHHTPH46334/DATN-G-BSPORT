package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dia_chi_khach_hang")
public class DiaChiKhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dia_chi_khach_hang")
    private Integer idDiaChiKhachHang;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang") // Ánh xạ khóa ngoại
    private KhachHang khachHang;

    @Column(name = "dia_chi_khach_hang" )
    private String diaChiKhachHang;
}
