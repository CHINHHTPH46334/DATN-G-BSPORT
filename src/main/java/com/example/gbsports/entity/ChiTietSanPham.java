package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Table(name = "chi_tiet_san_pham")
public class ChiTietSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_chi_tiet_san_pham;
    @ManyToOne
    @JoinColumn(name = "id_san_pham")
    SanPham sanPham;
    private String qr_code;
    private float gia_ban;
    private Integer so_luong;
    private String trang_thai;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_tao;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_sua;
    private float gia_nhap;
    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc")
    KichThuoc kichThuoc;
    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    MauSac mauSac;
}
