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
@Table(name = "voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voucher")
    private Integer id;

    @Column(name = "ma_voucher")
    private String maVoucher;

    @Column(name = "ten_voucher")
    private String tenVoucher;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // Định dạng datetime từ form
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_het_han")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ngayHetHan;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "gia_tri_toi_thieu")
    private BigDecimal giaTriToiThieu;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "kieu_giam_gia")
    private String kieuGiamGia;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "gia_tri_toi_da")
    private BigDecimal giaTriToiDa;
}
