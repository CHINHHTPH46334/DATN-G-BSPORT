package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "ma_voucher", unique = true, nullable = false, length = 50)
    private String maVoucher;

    @Column(name = "ten_voucher", nullable = false, length = 255)
    private String tenVoucher;

    @Column(name = "ngay_tao", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_het_han", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDateTime ngayHetHan;

    @Column(name = "gia_tri_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "gia_tri_toi_thieu")
    private BigDecimal giaTriToiThieu;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "kieu_giam_gia", nullable = false, length = 50)
    private String kieuGiamGia;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "gia_tri_toi_da", precision = 18, scale = 2)
    private BigDecimal giaTriToiDa;
}
