package com.example.gbsports.response;

import com.example.gbsports.entity.ChiTietSanPham;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KhuyenMaiResponse {
    private Integer id;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private OffsetDateTime ngayBatDau;
    private OffsetDateTime ngayHetHan;
    private BigDecimal giaTriGiam;
    private String kieuGiamGia;
    private String trangThai;
    private BigDecimal giaTriToiDa;
    private List<ChiTietSanPham> chiTietSanPhams;
}