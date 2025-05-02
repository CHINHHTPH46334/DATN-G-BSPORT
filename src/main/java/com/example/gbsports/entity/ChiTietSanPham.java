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
    private SanPham sanPham;
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
    @PrePersist @PreUpdate
    private void checkSoLuong(){
        if (this.so_luong <= 0){
            this.trang_thai = "Không hoạt động";
        }
    }

    public void inThongTin(){
        System.out.println("ChiTietSanPham{" +
                "id_chi_tiet_san_pham=" + id_chi_tiet_san_pham +
                ", sanPham=" + sanPham +
                ", qr_code='" + qr_code + '\'' +
                ", gia_ban=" + gia_ban +
                ", so_luong=" + so_luong +
                ", trang_thai='" + trang_thai + '\'' +
                ", ngay_tao=" + ngay_tao +
                ", ngay_sua=" + ngay_sua +
                ", kichThuoc=" + kichThuoc +
                ", mauSac=" + mauSac +
                '}');
    }
}
