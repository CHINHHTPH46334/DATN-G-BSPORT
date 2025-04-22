package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "hoi_thoai")
public class HoiThoai {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;
    @ManyToOne
    private NhanVien nhanVien;
    private Long thoiGianTao;
    private Long thoiGianCapNhat;
    private String ma;

}