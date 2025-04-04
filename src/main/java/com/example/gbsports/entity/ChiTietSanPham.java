package com.example.gbsports.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
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
    @NotNull(message = "Không để trống sản phẩm")
    @Valid
    SanPham sanPham;
    private String qr_code;
    private BigDecimal gia_ban;
    private Integer so_luong;
    private String trang_thai;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngay_tao;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngay_sua;
    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc")
    KichThuoc kichThuoc;
    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    MauSac mauSac;


}
