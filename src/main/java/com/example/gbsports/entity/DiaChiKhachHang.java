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
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "so_nha")
    private String soNha;

    @Column(name = "xa_phuong")
    private String xaPhuong;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;

    @Column(name = "dia_chi_mac_dinh")
    private Boolean diaChiMacDinh;

    // Phương thức để lấy địa chỉ đầy đủ
    public String getDiaChiKhachHang() {
        String soNhaSafe = soNha != null ? soNha : "";
        String xaPhuongSafe = xaPhuong != null ? xaPhuong : "";
        String quanHuyenSafe = quanHuyen != null ? quanHuyen : "";
        String tinhThanhPhoSafe = tinhThanhPho != null ? tinhThanhPho : "";
        return String.format("%s, %s, %s, %s", soNhaSafe, xaPhuongSafe, quanHuyenSafe, tinhThanhPhoSafe);
    }
}