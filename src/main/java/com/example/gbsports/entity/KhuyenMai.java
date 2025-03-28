package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "khuyen_mai")

public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khuyen_mai")
    private Integer id;

    @Column(name = "ma_khuyen_mai")
    private String maKhuyenMai;

    @Column(name = "ten_khuyen_mai")
    private String tenKhuyenMai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_bat_dau")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // Định dạng datetime từ form
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_het_han")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ngayHetHan;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "kieu_giam_gia")
    private String kieuGiamGia;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "gia_tri_toi_da")
    private BigDecimal giaTriToiDa;
}