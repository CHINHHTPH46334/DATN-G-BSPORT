package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
    private OffsetDateTime ngayBatDau;

    @Column(name = "ngay_het_han" )
    private OffsetDateTime ngayHetHan;
    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "kieu_giam_gia")
    private String kieuGiamGia;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "gia_tri_toi_da")
    private BigDecimal giaTriToiDa;
}