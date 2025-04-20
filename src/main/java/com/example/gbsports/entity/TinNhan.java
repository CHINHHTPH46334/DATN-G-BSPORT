package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tin_nhan")
public class TinNhan {
    @Id
    private Integer id;
    private Integer idNguoiGui;
    private String loaiNguoiGui; // "khach_hang" hoặc "nhan_vien"
    private String noiDung;
    private Long thoiGianTao;
    private Long thoiGianCapNhat;
    private String trangThai; // "da_gui" hoặc "da_doc"
    @ManyToOne
    @JoinColumn(name = "id_hoi_thoai")
    private HoiThoai hoiThoai;


}
