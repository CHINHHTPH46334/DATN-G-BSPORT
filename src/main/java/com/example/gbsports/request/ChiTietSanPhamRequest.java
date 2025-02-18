package com.example.gbsports.request;

import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.SanPham;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ChiTietSanPhamRequest {
    private Integer id_chi_tiet_san_pham;
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
    KichThuoc kichThuoc;
    MauSac mauSac;
}
